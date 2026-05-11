package com.sos.pingreport.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sos.pingreport.entity.ReportEntity;
import com.sos.pingreport.service.PdfService;
import com.sos.pingreport.util.PdfTemplateHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final TemplateEngine templateEngine;
    private final PdfTemplateHelper pdfTemplateHelper;

    @Override
    public byte[] generatePdf(String project, List<ReportEntity> reportEntities) throws Exception {

        Context context = new Context();
        context.setVariable("reportEntities", reportEntities);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a"));
        context.setVariable("generatedTime", time);
        // dynamic vars for template
        context.setVariable("projectName", pdfTemplateHelper.resolveProjectName(project));
        context.setVariable("tagLabel", pdfTemplateHelper.resolveTagLabel(project));

        String html = templateEngine.process("report", context);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        String baseUrl = Objects.requireNonNull(getClass().getResource("/static/")).toExternalForm();
        builder.withHtmlContent(html, baseUrl);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }
}