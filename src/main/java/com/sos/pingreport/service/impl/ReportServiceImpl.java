package com.sos.pingreport.service.impl;

import com.mongodb.client.MongoCollection;
import com.sos.pingreport.client.PythonClient;
import com.sos.pingreport.dto.ReportDTO;
import com.sos.pingreport.entity.ReportEntity;
import com.sos.pingreport.exception.MongoFetchException;
import com.sos.pingreport.mapper.ReportMapper;
import com.sos.pingreport.service.ReportService;
import com.sos.pingreport.util.AppConstantsHelper;
import com.sos.pingreport.util.MongoHelper;
import com.sos.pingreport.util.ReportFilterHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PythonClient pythonClient;
    private final ReportMapper reportMapper;
    private final MongoHelper mongoHelper;
    private final ReportFilterHelper reportFilterHelper;

    // process file
    @Override
    public List<ReportEntity> processFile(String project, MultipartFile file, String mode) {

        try {
            log.info("Processing file for project: {}", project);
            List<ReportDTO> response = pythonClient.processFile(project, file, mode);
            return response.stream()
                    .map(reportMapper::toReport)
                    .toList();
        } catch (Exception ex) {
            log.error("Failed to process file", ex);
            throw ex;
        }
    }

    // fetch from mongo
    @Override
    public List<ReportEntity> fetchFromMongo(String project) {

        try {
            MongoCollection<Document> collection = mongoHelper.getCollection(project);
            List<Document> docs = collection.find().into(new ArrayList<>());

            if (docs.isEmpty()) {
                throw new MongoFetchException("No data found in MongoDB");
            }

            String latestRunId = mongoHelper.getLatestRunId(docs);
            return docs.stream()
                    .filter(d -> latestRunId.equals(d.getString("runId")))
                    .map(reportMapper::fromMongo)
                    .toList();
        } catch (Exception ex) {
            log.error("Mongo fetch failed", ex);
            throw new MongoFetchException("Failed to fetch Mongo data");
        }
    }

    // apply mode filter
    @Override
    public List<ReportEntity> applyModeFilter(List<ReportEntity> reportEntities, String mode) {

        if (!"lite".equalsIgnoreCase(mode)) {
            return reportEntities;
        }
        return reportEntities.stream()
                .map(reportFilterHelper::filterLiteDevices)
                .toList();
    }

    // process combined
    @Override
    public List<ReportEntity> processCombined(MultipartFile file) {

        return processFile(AppConstantsHelper.COMBINED, file, "full");
    }
}