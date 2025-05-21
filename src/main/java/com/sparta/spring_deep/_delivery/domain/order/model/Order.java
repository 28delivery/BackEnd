package com.sparta.spring_deep._delivery.domain.order.model;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "p_order_status_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private OrderStatusEnum status = OrderStatusEnum.PENDING;

    @Column(name = "total_price", nullable = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(length = 50)
    @Size(max = 50)
    private String request;

    @Builder
    public Order(User customer, Restaurant restaurant, Address address,
        @Size(max = 50) String request) {
        super(customer.getUsername());
        this.customer = customer;
        this.restaurant = restaurant;
        this.address = address;
        this.request = request;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void updateTotalPrice() {
        this.totalPrice = orderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateOrderStatus(User user, OrderStatusEnum status) {
        super.update(user.getUsername()); // user -> username으로 변경 예정 (*baseEntity)
        this.status = status;
    }

    public enum OrderStatusEnum {
        PENDING,
        CONFIRMED,
        DELIVERED,
        FAILED,
        CANCELLED
    }

}
