package com.cine.demo.service.impl;

import com.cine.demo.dto.request.QuickRegisterDTO;
import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.RoomBookingRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.EmailService;
import com.cine.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final PurchaseRepository purchaseRepository;
    private final MerchandiseSaleRepository merchandiseSaleRepository;
    private final RoomBookingRepository roomBookingRepository;

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
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("A user already exists with email: " + dto.email());
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        User saved = userRepository.save(user);
        if (Boolean.TRUE.equals(dto.discountActive())) {
            try {
                emailService.sendMemberWelcome(saved.getEmail(), saved.getName());
            } catch (Exception e) {
                log.error("Failed to send member welcome email to {}: {}", saved.getEmail(), e.getMessage());
            }
        }
        return userMapper.toResponseDto(saved);
    }

    @Override
    public UserResponseDTO update(Long id, UpdateUserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (dto.email() != null && !dto.email().equals(user.getEmail())
                && userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("A user already exists with email: " + dto.email());
        }
        boolean wasActive = user.isDiscountActive();
        userMapper.updateEntityFromDto(dto, user);
        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        User saved = userRepository.save(user);
        if (!wasActive && Boolean.TRUE.equals(dto.discountActive())) {
            try {
                emailService.sendMemberWelcome(saved.getEmail(), saved.getName());
            } catch (Exception e) {
                log.error("Failed to send member welcome email to {}: {}", saved.getEmail(), e.getMessage());
            }
        }
        return userMapper.toResponseDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        purchaseRepository.detachUser(id);
        merchandiseSaleRepository.detachUser(id);
        roomBookingRepository.detachUser(id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getClients() {
        return userRepository.findByRole(Role.CLIENT).stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> search(String q) {
        return userRepository.search(q).stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchClients(String q) {
        return userRepository.searchByRole(q, Role.CLIENT).stream()
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

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDTO quickRegister(QuickRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("A user already exists with email: " + dto.email());
        }
        User user = User.builder()
                .name(dto.name())
                .lastName(dto.getLastName())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .birthDate(dto.getBirthDate())
                .role(Role.CLIENT)
                .annualVisits(0)
                .build();
        return userMapper.toResponseDto(userRepository.save(user));
    }
}