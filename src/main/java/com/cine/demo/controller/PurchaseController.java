package com.cine.demo.controller;

import com.cine.demo.dto.request.PaymentRequestDTO;
import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.ReservationRequestDTO;
import com.cine.demo.dto.request.TicketOfficeRequestDTO;
import com.cine.demo.dto.response.PaymentResponseDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.TicketOfficeResponseDTO;
import com.cine.demo.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    // ── Queries ──────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<PurchaseResponseDTO>> getAll() {
        return ResponseEntity.ok(purchaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(purchaseService.getByUser(userId));
    }

    @GetMapping("/screening/{screeningId}")
    public ResponseEntity<List<PurchaseResponseDTO>> getByScreening(@PathVariable Long screeningId) {
        return ResponseEntity.ok(purchaseService.getByScreening(screeningId));
    }

    // ── Admin CRUD (Reservations page) ────────────────────────────────────────

    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseService.createReservation(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO dto) {
        return ResponseEntity.ok(purchaseService.updateReservation(id, dto));
    }

    // ── Online purchase flow (seat-level) ─────────────────────────────────────

    @PostMapping("/online")
    public ResponseEntity<PurchaseResponseDTO> createOnline(
            @Valid @RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(dto));
    }

    @PostMapping("/ticket-office")
    public ResponseEntity<TicketOfficeResponseDTO> createFromTicketOffice(
            @Valid @RequestBody TicketOfficeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseService.createFromTicketOffice(dto));
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}/pay")
    public ResponseEntity<PaymentResponseDTO> pay(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(purchaseService.pay(id, dto));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseResponseDTO> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseResponseDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.cancel(id));
    }

    @PostMapping("/{id}/email")
    public ResponseEntity<Void> sendConfirmationEmail(@PathVariable Long id) {
        purchaseService.sendConfirmationEmail(id);
        return ResponseEntity.ok().build();
    }
}