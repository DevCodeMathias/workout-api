package com.devbackend.workoutapi.infrastructure.repository;

import com.devbackend.workoutapi.domain.repository.EmployeeRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoEmployeeRepository implements EmployeeRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoEmployeeRepository.class);

    private final MongoTemplate mongoTemplate;
    private final String employeesCollection;

    public MongoEmployeeRepository(
            MongoTemplate mongoTemplate,
            @Value("${mongodb.collections.employees}") String employeesCollection
    ) {
        this.mongoTemplate = mongoTemplate;
        this.employeesCollection = employeesCollection;
    }

    @Override
    public boolean existsById(String id) {
        if (!ObjectId.isValid(id)) {
            logger.warn("Employee id is not a valid ObjectId: {}", id);
            return false;
        }

        logger.debug("Checking employee existence by id: {}", id);
        Query query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        boolean exists = mongoTemplate.exists(query, employeesCollection);
        logger.debug("Employee existence result id={} exists={}", id, exists);
        return exists;
    }
}
