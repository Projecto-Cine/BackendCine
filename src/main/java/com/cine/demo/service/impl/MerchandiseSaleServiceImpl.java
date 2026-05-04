package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.MerchandiseSale;
import com.cine.demo.model.MerchandiseSaleItem;
import com.cine.demo.model.User;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.MerchandiseSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MerchandiseSaleServiceImpl implements MerchandiseSaleService {

    private final MerchandiseSaleRepository merchandiseSaleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MerchandiseSaleResponseDTO> findAll() {
        return merchandiseSaleRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MerchandiseSaleResponseDTO findById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Override
    public MerchandiseSaleResponseDTO save(MerchandiseSaleRequestDTO dto) {
        User cashier = dto.getCashierId() != null
                ? userRepository.findById(dto.getCashierId()).orElse(null)
                : null;

        MerchandiseSale sale = MerchandiseSale.builder()
                .total(dto.getTotal())
                .paymentMethod(dto.getPaymentMethod())
                .cashGiven(dto.getCashGiven())
                .change(dto.getChange())
                .cashier(cashier)
                .build();

        if (dto.getItems() != null) {
            List<MerchandiseSaleItem> items = dto.getItems().stream()
                    .map(i -> MerchandiseSaleItem.builder()
                            .sale(sale)
                            .productId(i.getProductId())
                            .name(i.getName())
                            .qty(i.getQty())
                            .unitPrice(i.getUnitPrice())
                            .build())
                    .toList();
            sale.getItems().addAll(items);
        }

        return toDto(merchandiseSaleRepository.save(sale));
    }

    @Override
    public MerchandiseSaleResponseDTO update(Long id, MerchandiseSaleRequestDTO dto) {
        MerchandiseSale sale = findOrThrow(id);
        if (dto.getTotal() != null) sale.setTotal(dto.getTotal());
        if (dto.getPaymentMethod() != null) sale.setPaymentMethod(dto.getPaymentMethod());
        return toDto(merchandiseSaleRepository.save(sale));
    }

    @Override
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

    private MerchandiseSaleResponseDTO toDto(MerchandiseSale sale) {
        return MerchandiseSaleResponseDTO.builder()
                .saleId(sale.getId())
                .total(sale.getTotal())
                .paymentMethod(sale.getPaymentMethod())
                .cashGiven(sale.getCashGiven())
                .change(sale.getChange())
                .createdAt(sale.getCreatedAt())
                .build();
    }
}