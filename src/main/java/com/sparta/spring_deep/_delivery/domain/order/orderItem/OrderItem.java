package com.sparta.spring_deep._delivery.domain.order.orderItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.menu.Menu;
import com.sparta.spring_deep._delivery.domain.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "p_order_item")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore // OrderItem을 응답할 때 order 정보 제외
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;


    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;


    public OrderItem(Order order, Menu menu, int quantity,
        @NotNull @Digits(integer = 10, fraction = 2) BigDecimal price) {
        super(order.getCustomer().getUsername());
        this.order = order;
        this.menu = menu;
        this.quantity = quantity;
        this.price = price;
    }

}
