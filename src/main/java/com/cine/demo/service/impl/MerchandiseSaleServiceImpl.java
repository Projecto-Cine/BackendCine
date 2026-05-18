package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.exception.BusinessRuleException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MerchandiseSaleMapper;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.MerchandiseSale;
import com.cine.demo.model.User;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.MerchandiseSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchandiseSaleServiceImpl implements MerchandiseSaleService {

    private final MerchandiseSaleRepository merchandiseSaleRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final UserRepository userRepository;
    private final MerchandiseSaleMapper merchandiseSaleMapper;

    @Override
    public List<MerchandiseSaleResponseDTO> findAll() {
        return merchandiseSaleRepository.findAll().stream()
                .map(merchandiseSaleMapper::toResponseDto)
                .toList();
    }

    @Override
    public MerchandiseSaleResponseDTO findById(Long id) {
        return merchandiseSaleMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public MerchandiseSaleResponseDTO save(MerchandiseSaleRequestDTO dto) {
        if (dto.userId() == null) throw new BusinessRuleException("User is required");
        if (dto.merchandiseId() == null) throw new BusinessRuleException("Item is required");
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.userId()));
        Merchandise merchandise = merchandiseRepository.findById(dto.merchandiseId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.merchandiseId()));

        if (merchandise.getStock() < dto.quantity()) {
            throw new BusinessRuleException("Insufficient stock. Available: " + merchandise.getStock());
        }

        merchandise.setStock(merchandise.getStock() - dto.quantity());
        merchandiseRepository.save(merchandise);

        BigDecimal total = merchandise.getPrice().multiply(BigDecimal.valueOf(dto.quantity()));
        MerchandiseSale sale = MerchandiseSale.builder()
                .user(user)
                .merchandise(merchandise)
                .quantity(dto.quantity())
                .total(total)
                .build();

        return merchandiseSaleMapper.toResponseDto(merchandiseSaleRepository.save(sale));
    }

    @Override
    @Transactional
    public MerchandiseSaleResponseDTO update(Long id, MerchandiseSaleRequestDTO dto) {
        MerchandiseSale sale = findOrThrow(id);
        if (dto.quantity() > 0) sale.setQuantity(dto.quantity());
        if (sale.getMerchandise() != null && dto.quantity() > 0) {
            sale.setTotal(sale.getMerchandise().getPrice().multiply(BigDecimal.valueOf(dto.quantity())));
        }
        return merchandiseSaleMapper.toResponseDto(merchandiseSaleRepository.save(sale));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!merchandiseSaleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sale not found with id: " + id);
        }
        merchandiseSaleRepository.deleteById(id);
    }

    private MerchandiseSale findOrThrow(Long id) {
        return merchandiseSaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
    }
}
