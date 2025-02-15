package com.sparta.spring_deep._delivery.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    private String id;
}
