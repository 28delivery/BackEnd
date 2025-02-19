package com.sparta.spring_deep._delivery.admin.repository;

import com.sparta.spring_deep._delivery.domain.ai.Ai;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID> {

    @Query("SELECT a FROM Ai a WHERE a.menu.restaurantId = :restaurantId")
    Page<Ai> findByRestaurantId(@Param("restaurantId") UUID restaurantId, Pageable pageable);
}
