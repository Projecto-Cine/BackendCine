package com.cine.demo.service.impl;

import com.cine.demo.dto.request.PaymentRequestDTO;
import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.ReservationRequestDTO;
import com.cine.demo.dto.request.TicketOfficeRequestDTO;
import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.PaymentResponseDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.TicketOfficeResponseDTO;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.exception.*;
import com.cine.demo.mapper.PurchaseMapper;
import com.cine.demo.model.*;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.model.enums.Role;
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

        if (!screening.getDateTime().isAfter(LocalDateTime.now())) {
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

            if (screeningSeat.isOccupied()) {
                throw new SeatAlreadyTakenException("El asiento " + seat.getRow() + seat.getNumber() + " ya está ocupado");
            }

            BigDecimal unitPrice = PriceCalculator.calculateUnitPrice(
                    screening.getBasePrice(), seat.getType(), ticketRequest.getTicketType());

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

        BigDecimal discountAmount = PriceCalculator.applyFidelityDiscount(adultSubtotal, user.getVisitsPerYear(), allTypes);
        boolean discountApplied = discountAmount.compareTo(BigDecimal.ZERO) > 0;

        purchase.setTotalAmount(subtotal.subtract(discountAmount));
        purchase.setDiscountAmount(discountAmount);
        purchase.setDiscountApplied(discountApplied);

        Purchase saved = purchaseRepository.save(purchase);
        return purchaseMapper.toResponseDto(saved);
    }

    @Override
    public PaymentResponseDTO pay(Long purchaseId, PaymentRequestDTO dto) {
        Purchase purchase = findOrThrow(purchaseId);

        if (purchase.getStatus() != PurchaseStatus.PENDING) {
            throw new InvalidPurchaseStatusException("Only PENDING purchases can be paid");
        }

        purchase.setPaymentMethod(dto.getPaymentMethod());

        String paymentQrCode = null;

        if (dto.getPaymentMethod() == PaymentMethod.QR) {
            // Generate a QR code for the customer to scan and pay
            paymentQrCode = String.format(
                    "LUMEN:PAY-%d|%.2f EUR|%s",
                    purchase.getId(),
                    purchase.getTotalAmount(),
                    purchase.getScreening().getMovie().getTitle()
            );
            // In a real integration this would call a payment provider and wait for confirmation.
            // For now the payment is confirmed immediately after QR generation.
        }

        purchase.setStatus(PurchaseStatus.CONFIRMED);
        User user = purchase.getUser();
        user.setVisitsPerYear(user.getVisitsPerYear() + 1);
        userRepository.save(user);
        Purchase saved = purchaseRepository.save(purchase);

        List<TicketResponseDTO> tickets = saved.getTickets().stream()
                .map(purchaseMapper::toTicketResponseDtoWithQr)
                .toList();

        return PaymentResponseDTO.builder()
                .purchaseId(saved.getId())
                .status(saved.getStatus())
                .paymentMethod(saved.getPaymentMethod())
                .totalAmount(saved.getTotalAmount())
                .discountAmount(saved.getDiscountAmount())
                .discountApplied(saved.isDiscountApplied())
                .movieTitle(saved.getScreening().getMovie().getTitle())
                .theaterName(saved.getScreening().getTheater().getName())
                .screeningDateTime(saved.getScreening().getDateTime())
                .tickets(tickets)
                .paymentQrCode(paymentQrCode)
                .build();
    }

    @Override
    public PurchaseResponseDTO createReservation(ReservationRequestDTO dto) {
        User client = resolveOrCreateClient(dto.getClientName(), dto.getClientEmail());

        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found: " + dto.getScreeningId()));

        Purchase purchase = Purchase.builder()
                .user(client)
                .screening(screening)
                .tickets(new ArrayList<>())
                .totalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO)
                .status(dto.getStatus() != null ? dto.getStatus() : PurchaseStatus.PENDING)
                .paymentMethod(dto.getPaymentMethod())
                .build();

        return purchaseMapper.toResponseDto(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponseDTO updateReservation(Long id, ReservationRequestDTO dto) {
        Purchase purchase = findOrThrow(id);

        if (dto.getClientEmail() != null && !dto.getClientEmail().equals(purchase.getUser().getEmail())) {
            purchase.setUser(resolveOrCreateClient(dto.getClientName(), dto.getClientEmail()));
        } else if (dto.getClientName() != null) {
            purchase.getUser().setName(dto.getClientName());
        }

        if (dto.getScreeningId() != null && !dto.getScreeningId().equals(purchase.getScreening().getId())) {
            Screening screening = screeningRepository.findById(dto.getScreeningId())
                    .orElseThrow(() -> new ResourceNotFoundException("Screening not found: " + dto.getScreeningId()));
            purchase.setScreening(screening);
        }

        if (dto.getStatus() != null) {
            PurchaseStatus newStatus = dto.getStatus();
            boolean wasntCancelled = purchase.getStatus() != PurchaseStatus.CANCELLED;
            if (newStatus == PurchaseStatus.CANCELLED && wasntCancelled) {
                for (Ticket ticket : purchase.getTickets()) {
                    screeningService.releaseSeat(ticket.getScreening().getId(), ticket.getSeat().getId());
                }
            }
            purchase.setStatus(newStatus);
        }

        if (dto.getPaymentMethod() != null) {
            purchase.setPaymentMethod(dto.getPaymentMethod());
        }

        if (dto.getTotalAmount() != null) {
            purchase.setTotalAmount(dto.getTotalAmount());
        }

        return purchaseMapper.toResponseDto(purchaseRepository.save(purchase));
    }

    private User resolveOrCreateClient(String name, String email) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            String username = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
            User guest = User.builder()
                    .name(name)
                    .username(username)
                    .email(email)
                    .password("")
                    .role(Role.CLIENT)
                    .status("active")
                    .visitsPerYear(0)
                    .build();
            return userRepository.save(guest);
        });
    }

    @Override
    public PurchaseResponseDTO confirm(Long purchaseId) {
        Purchase purchase = findOrThrow(purchaseId);

        if (purchase.getStatus() != PurchaseStatus.PENDING) {
            throw new InvalidPurchaseStatusException("Only PENDING purchases can be confirmed");
        }

        purchase.setStatus(PurchaseStatus.CONFIRMED);

        User user = purchase.getUser();
        user.setVisitsPerYear(user.getVisitsPerYear() + 1);
        userRepository.save(user);

        return purchaseMapper.toResponseDto(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponseDTO cancel(Long purchaseId) {
        Purchase purchase = findOrThrow(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.CANCELLED) {
            throw new PurchaseAlreadyCancelledException("La compra con id " + purchaseId + " ya está cancelada");
        }

        if (!purchase.getScreening().getDateTime().isAfter(LocalDateTime.now())) {
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

    @Override
    public TicketOfficeResponseDTO createFromTicketOffice(TicketOfficeRequestDTO dto) {
        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found with id: " + dto.getScreeningId()));

        User cashier = dto.getCashierId() != null
                ? userRepository.findById(dto.getCashierId()).orElse(null)
                : null;

        User effectiveUser = cashier != null ? cashier : userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No users found in the system"));

        Purchase purchase = Purchase.builder()
                .user(effectiveUser)
                .screening(screening)
                .tickets(new ArrayList<>())
                .totalAmount(dto.getTotal() != null ? BigDecimal.valueOf(dto.getTotal()) : BigDecimal.ZERO)
                .status(PurchaseStatus.CONFIRMED)
                .paymentMethod(dto.getPaymentMethod())
                .build();

        for (String seatLabel : dto.getSeats()) {
            String row = seatLabel.replaceAll("[0-9]", "");
            int number;
            try {
                number = Integer.parseInt(seatLabel.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                throw new ConflictException("Invalid seat format: " + seatLabel);
            }

            Seat seat = seatRepository.findByTheaterIdAndRowAndNumber(
                            screening.getTheater().getId(), row, number)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat " + seatLabel + " not found in this theater"));

            ScreeningSeat screeningSeat = screeningSeatRepository
                    .findByScreeningIdAndSeatId(screening.getId(), seat.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not available for this screening"));

            if (screeningSeat.isOccupied()) {
                throw new SeatAlreadyTakenException("Seat " + seatLabel + " is already taken");
            }

            BigDecimal unitPrice = dto.getUnitPrice() != null
                    ? BigDecimal.valueOf(dto.getUnitPrice() + (dto.getSurcharge() != null ? dto.getSurcharge() : 0))
                    : screening.getBasePrice();

            screeningSeat.setOccupied(true);
            screeningSeatRepository.save(screeningSeat);
            screening.setAvailableSeats(Math.max(0, screening.getAvailableSeats() - 1));

            Ticket ticket = Ticket.builder()
                    .purchase(purchase)
                    .seat(seat)
                    .screening(screening)
                    .ticketType(TicketType.ADULT)
                    .unitPrice(unitPrice)
                    .build();
            purchase.getTickets().add(ticket);
        }

        screeningRepository.save(screening);
        Purchase saved = purchaseRepository.save(purchase);

        List<String> qrCodes = saved.getTickets().stream()
                .map(ticket -> String.format(
                        "LUMEN:TKT-%d|%s|%s|%s|%s|%s|ADULT",
                        ticket.getId(),
                        screening.getMovie().getTitle(),
                        screening.getTheater().getName(),
                        screening.getDateTime().toLocalDate(),
                        screening.getDateTime().toLocalTime().toString().substring(0, 5),
                        ticket.getSeat().getRow() + ticket.getSeat().getNumber()
                ))
                .toList();

        return TicketOfficeResponseDTO.builder()
                .saleId(saved.getId())
                .qrCodes(qrCodes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public void sendConfirmationEmail(Long purchaseId) {
        emailService.sendPurchaseConfirmation(findOrThrow(purchaseId));
    }

    private Purchase findOrThrow(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
    }

    private void validateAgeRating(User user, Movie movie) {
        AgeRating rating = movie.getAgeRating();
        if (rating == AgeRating.ALL) return;

        int minAge = switch (rating) {
            case SEVEN -> 7;
            case TWELVE -> 12;
            case SIXTEEN -> 16;
            case EIGHTEEN -> 18;
            default -> 0;
        };

        if (minAge == 0) return;
        if (user.getDateOfBirth() == null) return;

        int userAge = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
        if (userAge < minAge) {
            throw new AgeRestrictionException(
                    "El usuario no cumple la edad mínima de " + minAge + " años para ver esta película");
        }
    }
}