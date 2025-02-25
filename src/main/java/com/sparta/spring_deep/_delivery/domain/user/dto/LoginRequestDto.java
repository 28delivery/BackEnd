package com.sparta.spring_deep._delivery.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {
    @NotBlank(message = "사용자 아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,10}$",
        message = "사용자 아이디는 영문 소문자, 숫자만 사용하여 4~10자리여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
        message = "비밀번호는 8~15자리여야 하며, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;
}
