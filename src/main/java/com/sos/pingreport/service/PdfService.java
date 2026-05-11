package com.sos.pingreport.service;

import com.sos.pingreport.entity.ReportEntity;

import java.util.List;

public interface PdfService {

    byte[] generatePdf(String project, List<ReportEntity> reportEntities) throws Exception;
}
