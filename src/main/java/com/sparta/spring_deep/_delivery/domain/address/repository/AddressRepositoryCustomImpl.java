package com.sparta.spring_deep._delivery.domain.address.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressResponseDto;
import com.sparta.spring_deep._delivery.domain.address.dto.AddressSearchDto;
import com.sparta.spring_deep._delivery.domain.address.entity.QAddress;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "AddressRepositoryQueryDSL")
public class AddressRepositoryCustomImpl implements AddressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AddressResponseDto> searchByOptionAndIsDeletedFalse(AddressSearchDto searchDto,
        User loggedInUser, Pageable pageable) {

        log.info("searchByOptionAndIsDeletedFalse");

        QAddress address = QAddress.address1;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        // 삭제되지 않은 주소만 조회
        builder.and(address.isDeleted.eq(false));
        // 사용자의 주소만 조회
        builder.and(address.user.eq(loggedInUser));

        // 주소 조건 (부분 일치, 대소문자 무시)
        if (searchDto.getAddress() != null && !searchDto.getAddress().isEmpty()) {
            builder.and(address.address.containsIgnoreCase(searchDto.getAddress()));
        }

        // 주소 별칭 (부분 일치, 대소문자 무시)
        if (searchDto.getAddressName() != null && !searchDto.getAddressName().isEmpty()) {
            builder.and(address.addressName.containsIgnoreCase(searchDto.getAddressName()));
        }

        // 주소 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<AddressResponseDto> content = queryFactory
            .select(Projections.constructor(
                AddressResponseDto.class,
                address.id,
                address.user.username,
                address.addressName,
                address.address
            ))
            .from(address)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(address.id)
            .from(address)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
