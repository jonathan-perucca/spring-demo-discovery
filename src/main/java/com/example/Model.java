package com.example;

import lombok.Getter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

@MappedSuperclass
@Getter
public class Model {
    @Id
    private String uuid;

    @PrePersist
    public void generateUuid() {
        this.uuid = UUID.randomUUID().toString();
    }
}
