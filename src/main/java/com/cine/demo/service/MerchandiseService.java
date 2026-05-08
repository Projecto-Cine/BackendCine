package com.cine.demo.service;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MerchandiseService {
    List<MerchandiseResponseDTO> findAll();
    List<MerchandiseResponseDTO> findActive();
    MerchandiseResponseDTO findById(Long id);
    MerchandiseResponseDTO save(MerchandiseRequestDTO dto, MultipartFile file);
    MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto, MultipartFile file);
    MerchandiseResponseDTO uploadImage(Long id, MultipartFile file);
    void delete(Long id);
}
