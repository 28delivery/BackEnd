package com.sparta.spring_deep._delivery.domain.category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "p_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID id;

    private String name;

    public Category(UUID uuid, String name) {
        this.id = uuid;
        this.name = name;
    }
}
