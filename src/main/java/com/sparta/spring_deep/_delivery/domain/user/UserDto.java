package com.sparta.spring_deep._delivery.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private IsPublic isPublic;
}
