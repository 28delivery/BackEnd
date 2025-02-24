package com.sparta.spring_deep._delivery.admin.user;

import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Data
public class UserCreateRequestDto {

    private String username;
    private String password;
    private String email;
    private UserRole role;
    private IsPublic isPublic;

}
