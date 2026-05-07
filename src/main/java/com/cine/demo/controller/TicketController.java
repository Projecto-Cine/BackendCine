package com.cine.demo.controller;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Consulta de tickets generados por las compras")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Listar tickets", description = "Acepta filtros opcionales: purchaseId o screeningId")
    public ResponseEntity<ApiResponse<List<TicketResponseDTO>>> getAll(
            @RequestParam(required = false) Long purchaseId,
            @RequestParam(required = false) Long screeningId) {
        List<TicketResponseDTO> data;
        if (purchaseId != null) {
            data = ticketService.getByPurchase(purchaseId);
        } else if (screeningId != null) {
            data = ticketService.getByScreening(screeningId);
        } else {
            data = ticketService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.<List<TicketResponseDTO>>builder()
                .success(true).message("Tickets obtenidos correctamente").data(data).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket por ID")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TicketResponseDTO>builder()
                .success(true).message("Ticket obtenido correctamente").data(ticketService.findById(id)).build());
    }
}
