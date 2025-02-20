package com.sparta.spring_deep._delivery.admin.repository;

import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantAddressAdminRepository extends JpaRepository<RestaurantAddress, UUID> {

    // 도로명 주소(roadAddr) 일부 검색
    Page<RestaurantAddress> findByRoadAddrContaining(String roadAddr, Pageable pageable);

    Optional<RestaurantAddress> findByRoadAddrAndDetailAddr(String roadAddr, String detailAddr);
}
