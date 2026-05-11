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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final PurchaseMapper purchaseMapper;
    private final ScreeningService screeningService;
    private final EmailService emailService;

    @Override
    public PurchaseResponseDTO create(PurchaseRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUserId()));

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada con id: " + dto.getScreeningId()));

        if (!screening.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("La proyección ya ha finalizado");
        }

        validateAgeRating(user, screening.getMovie());

        List<TicketRequestDTO> ticketRequests = dto.getTickets();
        boolean hasChild = ticketRequests.stream().anyMatch(t -> t.getTicketType() == TicketType.CHILD);
        boolean hasAdult = ticketRequests.stream().anyMatch(t -> t.getTicketType() == TicketType.ADULT);
        if (hasChild && !hasAdult) {
            throw new MinorWithoutAdultException("Un menor de edad debe ir acompañado de al menos un adulto en la misma compra");
        }

        Purchase purchase = Purchase.builder()
                .user(user)
                .screening(screening)
                .tickets(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (TicketRequestDTO ticketRequest : ticketRequests) {
            Seat seat = seatRepository.findById(ticketRequest.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado con id: " + ticketRequest.getSeatId()));

            if (!seat.getTheater().getId().equals(screening.getTheater().getId())) {
                throw new ConflictException("El asiento con id " + seat.getId() + " no pertenece a la sala de esta proyección");
            }

            ScreeningSeat screeningSeat = screeningSeatRepository
                    .findByScreeningIdAndSeatId(screening.getId(), seat.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asiento no disponible en esta proyección"));

            if (screeningSeat.isOcupado()) {
                throw new SeatAlreadyTakenException("El asiento " + seat.getFila() + seat.getNumero() + " ya está ocupado");
            }

            BigDecimal unitPrice = PriceCalculator.calculateUnitPrice(
                    screening.getPrecioBase(), seat.getTipo(), ticketRequest.getTicketType());

            screeningService.reserveSeat(screening.getId(), seat.getId());

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

        BigDecimal discountAmount = PriceCalculator.applyFidelityDiscount(adultSubtotal, user.getVisitasAnio(), allTypes);
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
            throw new InvalidPurchaseStatusException("Solo se pueden confirmar compras en estado PENDING");
        }

        purchase.setStatus(PurchaseStatus.PAID);

        User user = purchase.getUser();
        user.setVisitasAnio(user.getVisitasAnio() + 1);
        userRepository.save(user);

        Purchase saved = purchaseRepository.save(purchase);

        if (!saved.isEmailSent()) {
            try {
                emailService.sendPurchaseConfirmation(saved);
                saved.setEmailSent(true);
                purchaseRepository.save(saved);
            } catch (Exception e) {
                // email failure does not roll back the confirmed purchase
            }
        }

        return purchaseMapper.toResponseDto(saved);
    }

    @Override
    public PurchaseResponseDTO cancel(Long purchaseId) {
        Purchase purchase = findOrThrow(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.CANCELLED) {
            throw new PurchaseAlreadyCancelledException("La compra con id " + purchaseId + " ya está cancelada");
        }

        if (!purchase.getScreening().getFechaHora().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("No se puede cancelar una compra de una proyección que ya ha finalizado");
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
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
    }

    private void validateAgeRating(User user, Movie movie) {
        AgeRating rating = movie.getClasificacionEdad();
        if (rating == null || rating == AgeRating.ALL) return;

        int minAge = switch (rating) {
            case SEVEN -> 7;
            case TWELVE -> 12;
            case SIXTEEN -> 16;
            case EIGHTEEN -> 18;
            default -> 0;
        };

        if (minAge == 0 || user.getFechaNacimiento() == null) return;

        int userAge = Period.between(user.getFechaNacimiento(), LocalDate.now()).getYears();
        if (userAge < minAge) {
            throw new AgeRestrictionException(
                    "El usuario no cumple la edad mínima de " + minAge + " años para ver esta película");
        }
    }
}
