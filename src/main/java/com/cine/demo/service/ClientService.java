package com.cine.demo.service;

import com.cine.demo.dto.response.ClientSummaryDTO;
import java.util.List;

public interface ClientService {
    List<ClientSummaryDTO> getAll();
    ClientSummaryDTO getById(Long id);
    List<ClientSummaryDTO> search(String query);
}