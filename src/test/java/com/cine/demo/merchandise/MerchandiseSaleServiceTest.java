package com.cine.demo.merchandise;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.mapper.MerchandiseSaleMapper;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.impl.MerchandiseSaleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MerchandiseSaleServiceTest {

    @Mock private MerchandiseSaleRepository merchandiseSaleRepository;
    @Mock private MerchandiseRepository merchandiseRepository;
    @Mock private UserRepository userRepository;
    @Mock private MerchandiseSaleMapper merchandiseSaleMapper;

    @InjectMocks
    private MerchandiseSaleServiceImpl service;

    @Test
    void findAll_returnsEmptyList_whenNoSales() {
        assertThat(service.findAll()).isEmpty();
    }
}
