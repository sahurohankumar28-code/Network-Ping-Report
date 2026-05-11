package com.sos.pingreport.repository;

import com.sos.pingreport.entity.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {

    List<ReportEntity> findByStretch(String stretch);
}