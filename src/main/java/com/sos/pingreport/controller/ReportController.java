package com.sos.pingreport.controller;

import com.sos.pingreport.entity.ReportEntity;
import com.sos.pingreport.service.PdfService;
import com.sos.pingreport.service.ReportService;
import com.sos.pingreport.util.AppConstantsHelper;
import com.sos.pingreport.util.ResponseBuilderHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final PdfService pdfService;
    private final ResponseBuilderHelper responseBuilderHelper;

    // D2 / D3 upload + PDF
    @PostMapping(value = "/upload/{project}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadAndGeneratePdf(@PathVariable String project,
                                                       @RequestParam(value = "mode", defaultValue = "full")
                                                       String mode,
                                                       @RequestParam("file") MultipartFile file) throws Exception {
        log.info("Upload request received for project: {}", project);
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        List<ReportEntity> reportEntities = reportService.processFile(project, file, mode);
        byte[] pdf = pdfService.generatePdf(project, reportEntities);
        return responseBuilderHelper.buildPdfResponse(pdf, project + "-report.pdf");
    }

    // D2 / D3 mongo → PDF
    @GetMapping("/{project}/from-db")
    public ResponseEntity<byte[]> generateFromMongo(@PathVariable String project,
                                                    @RequestParam(defaultValue = "full") String mode) throws Exception {
        log.info("Mongo PDF request for project: {}", project);
        List<ReportEntity> reportEntities = reportService.fetchFromMongo(project);
        reportEntities = reportService.applyModeFilter(reportEntities, mode);

        if (reportEntities.isEmpty()) {
            throw new RuntimeException("No data available after filtering");
        }

        byte[] pdf = pdfService.generatePdf(project, reportEntities);
        return responseBuilderHelper.buildPdfResponse(pdf, project + "-report.pdf");
    }

    // combined (D2 and D3) upload + PDF
    @PostMapping(value = "/upload/combined", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> uploadCombined(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Combined upload request received");

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        List<ReportEntity> reportEntities = reportService.processCombined(file);
        byte[] pdf = pdfService.generatePdf(AppConstantsHelper.COMBINED, reportEntities);
        return responseBuilderHelper.buildPdfResponse(pdf, "combined-report.pdf");
    }

    // combined (D2 and D3) mongo → PDF
    @GetMapping("/combined/from-db")
    public ResponseEntity<byte[]> generateCombinedReport() throws Exception {

        log.info("Generating combined Mongo PDF");
        List<ReportEntity> reportEntities = reportService.fetchFromMongo(AppConstantsHelper.COMBINED);

        if (reportEntities.isEmpty()) {
            throw new RuntimeException("No combined data found");
        }

        byte[] pdf = pdfService.generatePdf(AppConstantsHelper.COMBINED, reportEntities);
        return responseBuilderHelper.buildPdfResponse(pdf, "combined-report.pdf");
    }

}