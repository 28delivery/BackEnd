package com.sparta.spring_deep._delivery.domain.restaurantAddress;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddress, UUID> {

    // 도로명 주소(roadAddr) 일부 검색
    List<RestaurantAddress> findByRoadAddrContaining(String roadAddr);
}
