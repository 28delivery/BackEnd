package com.sparta.spring_deep._delivery.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;
}
