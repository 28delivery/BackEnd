package com.sparta.spring_deep._delivery.domain.restaurantAddress.admin;

import com.sparta.spring_deep._delivery.domain.restaurantAddress.RestaurantAddress;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantAddressAdminRepository extends JpaRepository<RestaurantAddress, UUID> {

    // 도로명 주소(roadAddr) 일부 검색
    Page<RestaurantAddress> findByRoadAddrContaining(String roadAddr, Pageable pageable);
}
