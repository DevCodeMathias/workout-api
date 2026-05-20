package com.devBackend.workout_api.Infrastructure.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "${mongodb.collections.activities}")
public class Envelope<T> {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private T body;


    public Envelope() {
    }

    public Envelope(String id, LocalDateTime createdAt, LocalDateTime updatedAt, T body) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.body = body;
    }

    public static <T> Envelope<T> of(T body) {
        LocalDateTime now = LocalDateTime.now();
        return new Envelope<>(null, now, now, body);
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

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
