package com.sparta.spring_deep._delivery.domain.user.dto;

import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String username;

    private String password;

    @Email(message = "input valid e-mail form")
    @NotBlank(message = "input e-mail")
    private String email;

    private UserRole role;

    private IsPublic isPublic;
}
