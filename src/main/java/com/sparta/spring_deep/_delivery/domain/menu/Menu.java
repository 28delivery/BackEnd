package com.sparta.spring_deep._delivery.domain.menu;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_menu")
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // 테스트용으로 UUID 값을 사용. 후에 Restaurant 기능이 개발되면 @ManyToOne 및 밑에 코드 주석 해제 후 적용.
    //    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private UUID restaurantId;
//    private Restaurant restaurantId;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "is_hidden")
    private Boolean isHidden;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder
    public Menu(UUID restaurantId, String name, String description, BigDecimal price,
        Boolean isHidden, boolean isDeleted, User user) {
        super(user.getUsername());
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isHidden = isHidden;
        this.isDeleted = isDeleted;
    }

    public void update(String name,
        String description,
        BigDecimal price,
        Boolean isHidden,
        User user
    ) {
        super.update(user.getUsername());
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null) {
            this.price = price;
        }
        if (isHidden != null) {
            this.isHidden = isHidden;
        }
    }

    public void delete(User user) {
        super.delete(user.getUsername());
        this.isDeleted = true;
    }

}
