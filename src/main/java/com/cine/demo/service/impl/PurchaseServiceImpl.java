package com.cine.demo.service.impl;

import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    @Override
    public List<PurchaseResponseDTO> findAll() { return null; }

    @Override
    public PurchaseResponseDTO findById(Long id) { return null; }

    @Override
    public PurchaseResponseDTO save(PurchaseRequestDTO dto) { return null; }

    @Override
    public PurchaseResponseDTO update(Long id, PurchaseRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
