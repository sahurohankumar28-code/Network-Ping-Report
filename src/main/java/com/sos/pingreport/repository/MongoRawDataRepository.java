package com.sos.pingreport.repository;

import com.sos.pingreport.entity.MongoRawDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MongoRawDataRepository extends MongoRepository<MongoRawDataEntity, String> { }