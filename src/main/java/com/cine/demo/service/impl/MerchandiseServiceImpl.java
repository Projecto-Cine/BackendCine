package com.cine.demo.service.impl;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MerchandiseMapper;
import com.cine.demo.model.Merchandise;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.service.MerchandiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchandiseServiceImpl implements MerchandiseService {

    private final MerchandiseRepository merchandiseRepository;
    private final MerchandiseMapper merchandiseMapper;

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
        return merchandiseMapper.toResponseDto(
                merchandiseRepository.save(merchandiseMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public MerchandiseResponseDTO update(Long id, MerchandiseRequestDTO dto) {
        Merchandise entity = findOrThrow(id);
        merchandiseMapper.updateEntityFromDto(dto, entity);
        return merchandiseMapper.toResponseDto(merchandiseRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!merchandiseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artículo no encontrado con id: " + id);
        }
        merchandiseRepository.deleteById(id);
    }

    private Merchandise findOrThrow(Long id) {
        return merchandiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con id: " + id));
    }
}
