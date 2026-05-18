package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.enums.MerchandiseCategory;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchandiseServiceImpl implements MerchandiseService {

    private final MerchandiseRepository merchandiseRepository;

    @Override
    public List<MerchandiseResponseDTO> findAll() {
        return merchandiseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchandiseResponseDTO> findActive() {
        return merchandiseRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MerchandiseResponseDTO findById(Long id) {
        Merchandise merchandise = merchandiseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchandise not found"));
        return toDTO(merchandise);
    }

    @Override
    public MerchandiseResponseDTO save(MerchandiseRequestDTO dto) {
        Merchandise merchandise = Merchandise.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .minStock(dto.getMinStock())
                .supplier(dto.getSupplier())
                .imageUrl(dto.getImageUrl())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        merchandise = merchandiseRepository.save(merchandise);
        return toDTO(merchandise);
    }

    @Override
    public MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto) {
        Merchandise merchandise = merchandiseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchandise not found"));
        merchandise.setName(dto.getName());
        merchandise.setDescription(dto.getDescription());
        merchandise.setCategory(dto.getCategory());
        merchandise.setPrice(dto.getPrice());
        merchandise.setStock(dto.getStock());
        merchandise.setMinStock(dto.getMinStock());
        merchandise.setSupplier(dto.getSupplier());
        merchandise.setImageUrl(dto.getImageUrl());
        merchandise = merchandiseRepository.save(merchandise);
        return toDTO(merchandise);
    }

    @Override
    public void delete(Long id) {
        Merchandise merchandise = merchandiseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchandise not found"));
        merchandise.setActive(false);
        merchandiseRepository.save(merchandise);
    }

    private MerchandiseResponseDTO toDTO(Merchandise merchandise) {
        return MerchandiseResponseDTO.builder()
                .id(merchandise.getId())
                .name(merchandise.getName())
                .description(merchandise.getDescription())
                .category(merchandise.getCategory())
                .price(merchandise.getPrice())
                .stock(merchandise.getStock())
                .minStock(merchandise.getMinStock())
                .supplier(merchandise.getSupplier())
                .imageUrl(merchandise.getImageUrl())
                .active(merchandise.getActive())
                .createdAt(merchandise.getCreatedAt())
                .build();
    }
}
