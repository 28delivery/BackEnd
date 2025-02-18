package com.sparta.spring_deep._delivery.domain.menu;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findAllByRestaurantId(UUID uuid);

    //삭제 쿼리. 현재는 is_deleted 필드에 true 로 바꿔주고 삭제자는 포함되지 않음.
    @Transactional
    @Modifying
    @Query("UPDATE Menu m SET m.isDeleted = true, m.deletedAt = CURRENT_TIMESTAMP WHERE m.id = :menuId")
    void softDeleteMenu(@Param("menuId") UUID menuId);

}
