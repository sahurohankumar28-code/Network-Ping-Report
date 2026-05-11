package com.sos.pingreport.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportResponseDTO {
    private String tag;
    private String stretch;
    private String location;
    private List<String> failedDevices;
    private String status;
}