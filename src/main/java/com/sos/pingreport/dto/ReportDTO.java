package com.sos.pingreport.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportDTO {

    private String tag;
    private String id;
    private String stretch;
    private String location;
    private List<String> failedDevices;
}