package com.sparta.spring_deep._delivery.domain.user.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private final String token;
    private final String type = "Bearer";
    private final String username;
    private final List<String> roles;

    public LoginResponseDto(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}
