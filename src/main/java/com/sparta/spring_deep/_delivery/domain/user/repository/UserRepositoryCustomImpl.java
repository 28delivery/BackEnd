package com.sparta.spring_deep._delivery.domain.user.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.admin.user.UserAdminResponseDto;
import com.sparta.spring_deep._delivery.admin.user.UserAdminSearchDto;
import com.sparta.spring_deep._delivery.domain.user.entity.QUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "UserRepositoryQueryDSL")
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserAdminResponseDto> searchByOption(UserAdminSearchDto searchDto,
        Pageable pageable) {
        log.info("searchByOption");

        QUser user = QUser.user;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // ** Admin 용 조건 **
        // 생성 날짜 범위 검색
        builder.and(
            dateSearch(user.createdAt, searchDto.getCreatedFrom(), searchDto.getCreatedTo()));
        // 수정 날짜 범위 검색
        builder.and(
            dateSearch(user.updatedAt, searchDto.getUpdatedFrom(), searchDto.getUpdatedTo()));
        // 삭제 날짜 범위 검색
        builder.and(
            dateSearch(user.deletedAt, searchDto.getDeletedFrom(), searchDto.getDeletedTo()));
        // 삭제 여부 조회 (기본값 false)
        if (searchDto.getIsDeleted() == null || !searchDto.getIsDeleted()) {
            builder.and(user.isDeleted.eq(false));
        }

        // 유저 이름 검색
        if (searchDto.getUsername() != null && !searchDto.getUsername().isEmpty()) {
            builder.and(user.username.eq(searchDto.getUsername()));
        }

        // email 검색
        if (searchDto.getEmail() != null && !searchDto.getEmail().isEmpty()) {
            builder.and(user.email.eq(searchDto.getEmail()));
        }

        // role 검색
        if (searchDto.getRole() != null) {
            builder.and(user.role.eq(searchDto.getRole()));
        }

        // 공개 여부 검색
        if (searchDto.getIsPublic() != null) {
            builder.and(user.isPublic.eq(searchDto.getIsPublic()));
        }

        // 유저 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<UserAdminResponseDto> content = queryFactory
            .select(Projections.constructor(
                UserAdminResponseDto.class,
                user.username,
                user.password,
                user.email,
                user.role,
                user.isPublic,
                user.createdAt,
                user.createdBy,
                user.updatedAt,
                user.updatedBy,
                user.isDeleted,
                user.deletedAt,
                user.deletedBy
            ))
            .from(user)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(user.username)
            .from(user)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder dateSearch(DateTimePath<LocalDateTime> dateTime,
        LocalDateTime dateFrom, LocalDateTime dateTo) {
        BooleanBuilder builder = new BooleanBuilder();
        if (dateFrom != null && dateTo != null) {
            builder.and(dateTime.between(dateFrom, dateTo));
        } else if (dateFrom != null) {
            builder.and(dateTime.goe(dateFrom));
        } else if (dateTo != null) {
            builder.and(dateTime.loe(dateTo));
        }
        return builder;
    }
}
