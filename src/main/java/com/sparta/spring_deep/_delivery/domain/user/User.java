package com.sparta.spring_deep._delivery.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@RequiredArgsConstructor
@Table(name = "p_user")
public class User {

    @Id
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String role;

    @NotNull
    @ColumnDefault("FALSE")
    private boolean is_deleted;

    public User(String username, String password, String email, String role) {
        this.username = username;
    }
}
