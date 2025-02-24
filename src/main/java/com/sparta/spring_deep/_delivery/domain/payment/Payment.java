package com.sparta.spring_deep._delivery.domain.payment;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @JoinColumn(name = "amount", nullable = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "p_payment_payment_method_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "p_payment_method_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PaymentStatusEnum paymentStatus;

    public Payment(String username, Order order, BigDecimal amount) {
        super(username);
        this.paymentMethod = PaymentMethodEnum.CARD;
        this.paymentStatus = PaymentStatusEnum.PENDING;
        this.order = order;
        this.amount = amount;
    }

    public void completePayment() {
        this.paymentStatus = PaymentStatusEnum.COMPLETED;
    }

    public void cancelPayment() {
        this.paymentStatus = PaymentStatusEnum.CANCELED;
    }

    public void failPayment() {
        this.paymentStatus = PaymentStatusEnum.FAILED;
    }

    public enum PaymentMethodEnum {
        CARD
    }

    public enum PaymentStatusEnum {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELED
    }

}


