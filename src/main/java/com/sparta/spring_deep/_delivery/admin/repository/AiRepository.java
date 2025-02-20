package com.sparta.spring_deep._delivery.admin.repository;

import com.sparta.spring_deep._delivery.domain.ai.Ai;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID> {

    Page<Ai> findByMenuId(UUID menuId, Pageable pageable);

    Page<Ai> findByMenu_RestaurantId_Id(UUID restaurantId, Pageable pageable);
}
