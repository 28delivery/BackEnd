package com.sparta.spring_deep._delivery.admin.user;

import com.sparta.spring_deep._delivery.common.AdminSearchDto;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UserAdminSearchDto extends AdminSearchDto {

    private String username;
    private String email;
    private UserRole role;
    private IsPublic isPublic;

}

