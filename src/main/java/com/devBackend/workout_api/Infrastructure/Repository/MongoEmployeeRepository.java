package com.devBackend.workout_api.Infrastructure.Repository;

import com.devBackend.workout_api.Domain.Repository.EmployeeRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoEmployeeRepository implements EmployeeRepository {
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
            return false;
        }

        Query query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        return mongoTemplate.exists(query, employeesCollection);
    }
}
