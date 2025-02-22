package com.sparta.spring_deep._delivery.domain.restaurant;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.spring_deep._delivery.admin.restaurant.RestaurantAdminCreateRequestDto;
import com.sparta.spring_deep._delivery.admin.restaurant.RestaurantAdminRequestDto;
import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

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

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "category", nullable = false, columnDefinition = "p_restaurant_category_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private CategoryEnum category;

    @NotNull
    @OneToOne
    private RestaurantAddress restaurantAddress;

    private String phone;

    public Restaurant(RestaurantAdminCreateRequestDto restaurantAdminCreateRequestDto, User owner,
        RestaurantAddress restaurantAddress, String username) {
        super(username);
        this.owner = owner;
        this.category = restaurantAdminCreateRequestDto.getCategory();
        this.name = restaurantAdminCreateRequestDto.getName();
        this.phone = restaurantAdminCreateRequestDto.getPhone();
        this.restaurantAddress = restaurantAddress;
    }

    public void UpdateRestaurant(RestaurantRequestDto restaurantRequestDto,
        RestaurantAddress restaurantAddress, String username) {
        this.category = restaurantRequestDto.getCategory();
        this.name = restaurantRequestDto.getName();
        this.restaurantAddress = restaurantAddress;
        this.phone = restaurantRequestDto.getPhone();
        super.update(username);
    }

    public void UpdateRestaurant(RestaurantAdminRequestDto restaurantAdminRequestDto,
        RestaurantAddress restaurantAddress, String username) {
        this.category = restaurantAdminRequestDto.getCategory();
        this.name = restaurantAdminRequestDto.getName();
        this.restaurantAddress = restaurantAddress;
        this.phone = restaurantAdminRequestDto.getPhone();
        super.update(username);
    }

    public enum CategoryEnum {
        HANSIK("한식"),
        YANGSIK("양식"),
        JUNGSIK("중식"),
        ILSIK("일식");

        private final String label;

        CategoryEnum(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

}
