package com.cine.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentRequestDTO {

    private String title;
    private String category;
    private String priority;
    private String status;
    private String room;
    private String description;
    private String assignedTo;
    private String reportedBy;
}