package com.sparta.spring_deep._delivery.domain.user;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_user")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(name = "username",length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_public", nullable = false)
    private IsPublic isPublic;

    @Builder
    public User(String username, String password, String email, UserRole role, IsPublic isPublic) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isPublic = isPublic;
    }

}
