package com.sparta.spring_deep._delivery.domain.user.dto;

import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotBlank(message = "사용자 아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,10}$",
        message = "사용자 아이디는 영문 소문자, 숫자만 사용하여 4~10자리여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
        message = "비밀번호는 8~15자리여야 하며, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
        message = "이메일 형식이 올바르지 않습니다.")
    // 영문 대/소문자, 숫자, 특수문자, '+'는 앞의 패턴이 1회이상 반복 / @ 기호 필수 / .은 실제 점, 영문대소문자, 2~6자 길이 제한, $은 문자열의 끝이 최상위 도메인인지 체크
    private String email;

    private UserRole role;

    private IsPublic isPublic;
}
