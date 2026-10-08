package com.cine.demo.controller;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.MerchandiseSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cine.demo.security.access.BoxOffice;

@RestController
@RequestMapping("/api/merchandise/sales")
@RequiredArgsConstructor
@BoxOffice
public class ConcessionSalesController {

    private final MerchandiseSaleService merchandiseSaleService;

    @PostMapping
    public ResponseEntity<ApiResponse<MerchandiseSaleResponseDTO>> create(
            @Valid @RequestBody MerchandiseSaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Concession sale registered successfully", merchandiseSaleService.save(dto)));
    }
}
