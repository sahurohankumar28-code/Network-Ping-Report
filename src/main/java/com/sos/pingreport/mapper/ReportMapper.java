package com.sos.pingreport.mapper;

import com.sos.pingreport.dto.ReportDTO;
import com.sos.pingreport.entity.ReportEntity;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportMapper {

    public ReportEntity toReport(ReportDTO dto) {

        String tag = dto.getTag() != null ? dto.getTag() : dto.getId();

        return ReportEntity.builder()
                .tag(tag)
                .stretch(dto.getStretch())
                .location(dto.getLocation())
                .failedDevices(dto.getFailedDevices())
                .status(dto.getFailedDevices() == null ||
                        dto.getFailedDevices().isEmpty() ? "OK" : "DOWN")
                .build();
    }

    public ReportEntity fromMongo(Document doc) {

        String tag = doc.getString("TAG_ID") != null
                ? doc.getString("TAG_ID")
                : doc.getString("ID");

        String location = doc.getString("LAND_MARK") != null
                ? doc.getString("LAND_MARK")
                : doc.getString("LOCATION");

        @SuppressWarnings("unchecked")
        List<String> failed = (List<String>) doc.get("failedDevices");

        if (failed == null) failed = new ArrayList<>();

        return ReportEntity.builder()
                .tag(tag)
                .stretch(doc.getString("Stretch"))
                .location(location)
                .failedDevices(failed)
                .status(doc.getString("status"))
                .build();
    }
}