package com.sparta.spring_deep._delivery.domain.restaurant.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantAdminRequestDto;
import com.sparta.spring_deep._delivery.domain.restaurant.dto.RestaurantRequestDto;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_restaurant")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotNull
    @Column(unique = true)
    @Size(max = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private Category category;

    @NotNull
    private String address;

    private String phone;

    public Restaurant(RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto, User owner,
        Category category, String username) {
        super(username);
        this.owner = owner;
        this.category = category;
        this.name = restaurantAdminCreateRequestDto.getName();
//        this.address = address;
        this.address = restaurantAdminCreateRequestDto.getAddress();
        this.phone = restaurantAdminCreateRequestDto.getPhone();
    }

    public void UpdateRestaurant(RestaurantRequestDto restaurantRequestDto, Category categoryId,
        String username) {
        this.category = categoryId;
        this.name = restaurantRequestDto.getName();
//        this.address = address;
        this.address = restaurantRequestDto.getAddress();
        this.phone = restaurantRequestDto.getPhone();
        super.update(username);
    }

    public void UpdateRestaurant(RestaurantAdminRequestDto restaurantAdminRequestDto,
        Category categoryId, String username) {
        this.category = categoryId;
        this.name = restaurantAdminRequestDto.getName();
//        this.address = address;
        this.address = restaurantAdminRequestDto.getAddress();
        this.phone = restaurantAdminRequestDto.getPhone();
        super.update(username);
    }

}
