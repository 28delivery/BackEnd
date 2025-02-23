package com.sparta.spring_deep._delivery.admin.ai;

import com.sparta.spring_deep._delivery.common.AdminSearchDto;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class AiLogSearchDto extends AdminSearchDto {

    UUID id;
    UUID menuId;
    UUID restaurantId;
    String restaurantName;
    String request;
    String response;
}
