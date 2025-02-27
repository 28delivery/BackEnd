package com.sparta.spring_deep._delivery.domain.address.repository;

import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, UUID>, AddressRepositoryCustom {

    // 특정 주소 ID로 조회 (단건 조회)
    Optional<Address> findById(UUID id);

    // 특정 사용자의 모든 주소 조회
    List<Address> findAllByUserUsername(String username);

    // 특정 주소 삭제
    void deleteById(UUID id);

    Optional<Address> findByIdAndIsDeletedFalse(UUID addressId);

    List<Address> findAllByUserUsernameAndIsDeletedFalse(String username);


    boolean existsByAddressName(@NotNull String addressName);
}