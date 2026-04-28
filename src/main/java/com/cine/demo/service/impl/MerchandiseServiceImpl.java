package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchandiseServiceImpl implements MerchandiseService {

    @Override
    public List<MerchandiseResponseDTO> findAll() { return null; }

    @Override
    public MerchandiseResponseDTO findById(Long id) { return null; }

    @Override
    public MerchandiseResponseDTO save(MerchandiseRequestDTO dto) { return null; }

    @Override
    public MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) {}
}
