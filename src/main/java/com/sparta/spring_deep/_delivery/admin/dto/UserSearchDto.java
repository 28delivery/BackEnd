package com.sparta.spring_deep._delivery.admin.dto;

import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSearchDto {

    private String username;
    private String password;
    private String email;
    private UserRole role;
    private IsPublic isPublic;

    // 페이징
    private int page = 0;
    private int size = 10;

}

