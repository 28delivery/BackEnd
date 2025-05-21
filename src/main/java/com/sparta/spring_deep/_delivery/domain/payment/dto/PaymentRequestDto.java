package com.sparta.spring_deep._delivery.domain.payment.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class PaymentRequestDto {

    String orderId;
    BigDecimal amount;

}
