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
        if (dto.getUserId() == null) throw new BusinessRuleException("El usuario es obligatorio");
        if (dto.getMerchandiseId() == null) throw new BusinessRuleException("El artículo es obligatorio");
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUserId()));
        Merchandise merchandise = merchandiseRepository.findById(dto.getMerchandiseId())
                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con id: " + dto.getMerchandiseId()));

        if (merchandise.getStock() < dto.getQuantity()) {
            throw new BusinessRuleException("Stock insuficiente. Disponible: " + merchandise.getStock());
        }

        merchandise.setStock(merchandise.getStock() - dto.getQuantity());
        merchandiseRepository.save(merchandise);

        BigDecimal total = merchandise.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        MerchandiseSale sale = MerchandiseSale.builder()
                .user(user)
                .merchandise(merchandise)
                .quantity(dto.getQuantity())
                .total(total)
                .build();

        return merchandiseSaleMapper.toResponseDto(merchandiseSaleRepository.save(sale));
    }

    @Override
    @Transactional
    public MerchandiseSaleResponseDTO update(Long id, MerchandiseSaleRequestDTO dto) {
        MerchandiseSale sale = findOrThrow(id);
        if (dto.getQuantity() > 0) sale.setQuantity(dto.getQuantity());
        if (sale.getMerchandise() != null && dto.getQuantity() > 0) {
            sale.setTotal(sale.getMerchandise().getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }
        return merchandiseSaleMapper.toResponseDto(merchandiseSaleRepository.save(sale));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!merchandiseSaleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venta no encontrada con id: " + id);
        }
        merchandiseSaleRepository.deleteById(id);
    }

    private MerchandiseSale findOrThrow(Long id) {
        return merchandiseSaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }
}
