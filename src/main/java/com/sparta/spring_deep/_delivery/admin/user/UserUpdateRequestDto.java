package com.sparta.spring_deep._delivery.admin.user;

import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String email;
    private UserRole role;
    private IsPublic isPublic;

}

