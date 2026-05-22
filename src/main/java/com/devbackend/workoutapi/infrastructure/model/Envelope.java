package com.devbackend.workoutapi.infrastructure.model;

import com.devbackend.workoutapi.domain.entity.Activity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "${mongodb.collections.activities}")
public class Envelope {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Activity body;


    public Envelope() {
    }

    public Envelope(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Activity body) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.body = body;
    }

    public static Envelope of(Activity body) {
        LocalDateTime now = LocalDateTime.now();
        return new Envelope(null, now, now, body);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Activity getBody() {
        return body;
    }

    public void setBody(Activity body) {
        this.body = body;
    }
}
