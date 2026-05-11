package com.cine.demo.service.impl;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("A user already exists with email: " + dto.getEmail());
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDTO update(Long id, UpdateUserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("A user already exists with email: " + dto.getEmail());
        }
        userMapper.updateEntityFromDto(dto, user);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> search(String q) {
        return userRepository.search(q).stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDTO uploadImage(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        String imageUrl = cloudinaryService.uploadImage(file, "users");
        user.setImageUrl(imageUrl);
        return userMapper.toResponseDto(userRepository.save(user));
    }
}
