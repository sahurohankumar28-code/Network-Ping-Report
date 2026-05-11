package com.sos.pingreport.service;

import com.sos.pingreport.entity.ReportEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {

    public List<ReportEntity> processFile(String project, MultipartFile file, String mode);

    List<ReportEntity> fetchFromMongo(String project);

    public List<ReportEntity> applyModeFilter(List<ReportEntity> reportEntities, String mode);

    public List<ReportEntity> processCombined(MultipartFile file);
}