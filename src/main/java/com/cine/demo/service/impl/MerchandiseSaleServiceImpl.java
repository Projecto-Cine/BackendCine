package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.MerchandiseSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchandiseSaleServiceImpl implements MerchandiseSaleService {

    @Override
    public List<MerchandiseSaleResponseDTO> findAll() { return null; }

    @Override
    public MerchandiseSaleResponseDTO findById(Long id) { return null; }

    @Override
    public MerchandiseSaleResponseDTO save(MerchandiseSaleRequestDTO dto) { return null; }

    @Override
    public MerchandiseSaleResponseDTO update(Long id, MerchandiseSaleRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
