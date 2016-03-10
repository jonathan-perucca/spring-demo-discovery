package com.example;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

@MappedSuperclass
@Getter
@ToString
public abstract class Model {
    @Id
    private String uuid;

    @PrePersist
    public void generateUuid() {
        this.uuid = UUID.randomUUID().toString();
    }
}
