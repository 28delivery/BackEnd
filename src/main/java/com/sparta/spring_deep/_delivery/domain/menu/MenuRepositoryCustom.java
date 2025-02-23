package com.sparta.spring_deep._delivery.domain.menu;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuRepositoryCustom {

    Page<Menu> searchMenusByRestaurantId(UUID restaurantId, String menuName,
        Pageable pageable);
}
