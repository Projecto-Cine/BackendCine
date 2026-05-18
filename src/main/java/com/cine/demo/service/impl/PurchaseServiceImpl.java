package com.cine.demo.service.impl;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.exception.*;
import com.cine.demo.mapper.PurchaseMapper;
import com.cine.demo.model.*;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.model.enums.TicketType;
import com.cine.demo.repository.*;
import com.cine.demo.service.EmailService;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ScreeningService;
import com.cine.demo.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final PurchaseMapper purchaseMapper;
    private final ScreeningService screeningService;
    private final EmailService emailService;

    @Override
    public PurchaseResponseDTO create(PurchaseRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found with id: " + dto.getScreeningId()));

        if (!screening.getStartTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("The screening has already ended");
        }

        validateAgeRating(user, screening.getMovie());

        List<TicketRequestDTO> ticketRequests = dto.getTickets();
        boolean hasChild = ticketRequests.stream().anyMatch(t -> t.getTicketType() == TicketType.CHILD);
        boolean hasAdult = ticketRequests.stream().anyMatch(t -> t.getTicketType() == TicketType.ADULT);
        if (hasChild && !hasAdult) {
            throw new MinorWithoutAdultException("A child must be accompanied by at least one adult in the same purchase");
        }
        if (hasChild && hasAdult && !isAdultAge(user)) {
            throw new MinorWithoutAdultException("The buyer must be an adult to accompany a minor");
        }

        Purchase purchase = Purchase.builder()
                .user(user)
                .screening(screening)
                .tickets(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .paymentMethod(dto.getPaymentMethod())
                .guestEmail(dto.getGuestEmail())
                .build();

        for (TicketRequestDTO ticketRequest : ticketRequests) {
            ScreeningSeat screeningSeat = screeningSeatRepository
                    .findById(ticketRequest.getScreeningSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("ScreeningSeat not found with id: " + ticketRequest.getScreeningSeatId()));

            if (!screeningSeat.getScreening().getId().equals(screening.getId())) {
                throw new ConflictException("ScreeningSeat " + ticketRequest.getScreeningSeatId() + " does not belong to this screening");
            }

            Seat seat = screeningSeat.getSeat();

            if (screeningSeat.isEffectivelyTaken()) {
                throw new SeatAlreadyTakenException("Seat " + seat.getRow() + seat.getNumber() + " is already occupied or reserved");
            }

            BigDecimal unitPrice = PriceCalculator.calculateUnitPrice(
                    screening.getBasePrice(), seat.getType(), ticketRequest.getTicketType());

            screeningService.tempReserveSeat(screening.getId(), seat.getId());

            Ticket ticket = Ticket.builder()
                    .purchase(purchase)
                    .seat(seat)
                    .screening(screening)
                    .ticketType(ticketRequest.getTicketType())
                    .unitPrice(unitPrice)
                    .build();
            purchase.getTickets().add(ticket);
        }

        BigDecimal subtotal = purchase.getTickets().stream()
                .map(Ticket::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal adultSubtotal = purchase.getTickets().stream()
                .filter(t -> t.getTicketType() == TicketType.ADULT)
                .map(Ticket::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TicketType> allTypes = purchase.getTickets().stream()
                .map(Ticket::getTicketType)
                .toList();

        BigDecimal discountAmount = PriceCalculator.applyFidelityDiscount(adultSubtotal, user.getAnnualVisits(), allTypes);
        boolean discountApplied = discountAmount.compareTo(BigDecimal.ZERO) > 0;

        purchase.setTotalAmount(subtotal.subtract(discountAmount));
        purchase.setDiscountAmount(discountAmount);
        purchase.setDiscountApplied(discountApplied);

        Purchase saved = purchaseRepository.save(purchase);
        return purchaseMapper.toResponseDto(saved);
    }

    @Override
    public PurchaseResponseDTO confirm(Long purchaseId) {
        Purchase purchase = findOrThrow(purchaseId);

        if (purchase.getStatus() != PurchaseStatus.PENDING) {
            throw new InvalidPurchaseStatusException("Only purchases in PENDING status can be confirmed");
        }

        // Convert temp reservations to permanent occupation
        for (Ticket ticket : purchase.getTickets()) {
            screeningService.reserveSeat(ticket.getScreening().getId(), ticket.getSeat().getId());
        }

        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setPaidAt(java.time.LocalDateTime.now());

        User user = purchase.getUser();
        user.setAnnualVisits(user.getAnnualVisits() + 1);
        userRepository.save(user);

        Purchase saved = purchaseRepository.save(purchase);

        if (!saved.isEmailSent()) {
            try {
                emailService.sendPurchaseConfirmation(saved);
                saved.setEmailSent(true);
                purchaseRepository.save(saved);
            } catch (Exception e) {
                log.error("Email sending failed for purchase {}: {}", saved.getId(), e.getMessage());
            }
        }

        return purchaseMapper.toResponseDto(saved);
    }

    @Override
    public PurchaseResponseDTO cancel(Long purchaseId) {
        Purchase purchase = findOrThrow(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.CANCELLED) {
            throw new PurchaseAlreadyCancelledException("Purchase with id " + purchaseId + " is already cancelled");
        }

        if (!purchase.getScreening().getStartTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("Cannot cancel a purchase for a screening that has already ended");
        }

        for (Ticket ticket : purchase.getTickets()) {
            screeningService.releaseSeat(ticket.getScreening().getId(), ticket.getSeat().getId());
        }

        purchase.setStatus(PurchaseStatus.CANCELLED);
        return purchaseMapper.toResponseDto(purchaseRepository.save(purchase));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponseDTO getById(Long id) {
        return purchaseMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getAll() {
        return purchaseRepository.findAll().stream()
                .map(purchaseMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getByUser(Long userId) {
        return purchaseRepository.findByUserId(userId).stream()
                .map(purchaseMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getByScreening(Long screeningId) {
        return purchaseRepository.findByScreeningId(screeningId).stream()
                .map(purchaseMapper::toResponseDto)
                .toList();
    }

    private Purchase findOrThrow(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
    }

    private boolean isAdultAge(User user) {
        if (user.getBirthDate() == null) return false;
        return Period.between(user.getBirthDate(), LocalDate.now()).getYears() >= 18;
    }

    private void validateAgeRating(User user, Movie movie) {
        AgeRating rating = movie.getAgeRating();
        if (rating == null || rating == AgeRating.ALL) return;

        int minAge = switch (rating) {
            case SEVEN -> 7;
            case TWELVE -> 12;
            case SIXTEEN -> 16;
            case EIGHTEEN -> 18;
            default -> 0;
        };

        if (minAge == 0 || user.getBirthDate() == null) return;

        int userAge = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        if (userAge < minAge) {
            throw new AgeRestrictionException(
                    "User does not meet the minimum age of " + minAge + " years to watch this movie");
        }
    }
}
