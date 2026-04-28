package com.cine.demo.service;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import java.util.List;

public interface MerchandiseService {
    List<MerchandiseResponseDTO> findAll();
    MerchandiseResponseDTO findById(Long id);
    MerchandiseResponseDTO save(MerchandiseRequestDTO dto);
    MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto);
    void delete(Long id);
}
