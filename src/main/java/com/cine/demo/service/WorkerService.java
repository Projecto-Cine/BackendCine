package com.cine.demo.service;

import com.cine.demo.dto.request.UpdateWorkerRequestDTO;
import com.cine.demo.dto.request.WorkerRequestDTO;
import com.cine.demo.dto.response.WorkerResponseDTO;

import java.util.List;

public interface WorkerService {
    List<WorkerResponseDTO> getAll();
    WorkerResponseDTO getById(Long id);
    WorkerResponseDTO create(WorkerRequestDTO dto);
    WorkerResponseDTO update(Long id, UpdateWorkerRequestDTO dto);
    void delete(Long id, Long requesterId);
}