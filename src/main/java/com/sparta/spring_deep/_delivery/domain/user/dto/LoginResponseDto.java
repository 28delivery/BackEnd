package com.sparta.spring_deep._delivery.domain.user.dto;

import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponseDto {
    private final String token;
    private final String username;
    private final String email;
    private final UserRole role;
    private final IsPublic isPublic;
}
