package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MerchandiseMapper;
import com.cine.demo.model.Merchandise;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchandiseServiceImpl implements MerchandiseService {

    private final MerchandiseRepository merchandiseRepository;
    private final MerchandiseMapper merchandiseMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<MerchandiseResponseDTO> findActive() {
        return merchandiseRepository.findByActiveTrue().stream()
                .map(merchandiseMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<MerchandiseResponseDTO> findAll() {
        return merchandiseRepository.findAll().stream()
                .map(merchandiseMapper::toResponseDto)
                .toList();
    }

    @Override
    public MerchandiseResponseDTO findById(Long id) {
        return merchandiseMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public MerchandiseResponseDTO save(MerchandiseRequestDTO dto) {
        return save(dto, null);
    }

    @Override
    @Transactional
    public MerchandiseResponseDTO save(MerchandiseRequestDTO dto, MultipartFile file) {
        Merchandise entity = merchandiseMapper.toEntity(dto);
        Merchandise saved = merchandiseRepository.save(entity);
        if (file != null && !file.isEmpty()) {
            saved.setImageUrl(cloudinaryService.uploadImage(file, "merchandise"));
            saved = merchandiseRepository.save(saved);
        }
        return merchandiseMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto) {
        return update(id, dto, null);
    }

    @Override
    @Transactional
    public MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto, MultipartFile file) {
        Merchandise entity = findOrThrow(id);
        merchandiseMapper.updateEntityFromDto(dto, entity);
        if (file != null && !file.isEmpty()) {
            entity.setImageUrl(cloudinaryService.uploadImage(file, "merchandise"));
        }
        return merchandiseMapper.toResponseDto(merchandiseRepository.save(entity));
    }

    @Override
    @Transactional
    public MerchandiseResponseDTO uploadImage(Long id, MultipartFile file) {
        Merchandise entity = findOrThrow(id);
        entity.setImageUrl(cloudinaryService.uploadImage(file, "merchandise"));
        return merchandiseMapper.toResponseDto(merchandiseRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!merchandiseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        merchandiseRepository.deleteById(id);
    }

    private Merchandise findOrThrow(Long id) {
        return merchandiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
}
