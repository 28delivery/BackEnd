package com.sparta.spring_deep._delivery.domain.payment.dto;

import com.sparta.spring_deep._delivery.domain.payment.model.Payment;
import com.sparta.spring_deep._delivery.domain.payment.model.Payment.PaymentMethodEnum;
import com.sparta.spring_deep._delivery.domain.payment.model.Payment.PaymentStatusEnum;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponseDto {

    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum paymentStatus;

    public PaymentResponseDto(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrder().getId();
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentStatus = payment.getPaymentStatus();
    }

}
