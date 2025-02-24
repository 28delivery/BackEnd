package com.sparta.spring_deep._delivery.admin.order;

import com.sparta.spring_deep._delivery.common.AdminSearchDto;
import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class OrderAdminSearchDto extends AdminSearchDto {

    private UUID id;
    private String customerId;
    private UUID restaurantId;
    private String restaurantName;
    private OrderStatusEnum status;

}
