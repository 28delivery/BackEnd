package com.sparta.spring_deep._delivery.admin.ai;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.ai.QAi;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "AiRepositoryQueryDSL")
public class AiRepositoryCustomImpl implements AiRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AiLogResponseDto> searchByOption(AiLogSearchDto searchDto, Pageable pageable) {
        log.info("searchByOption");

        QAi ai = QAi.ai;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // ** Admin 용 조건 **
        // 생성 날짜 범위 검색
        builder.and(dateSearch(ai.createdAt, searchDto.getCreatedFrom(), searchDto.getCreatedTo()));
        // 수정 날짜 범위 검색
        builder.and(dateSearch(ai.updatedAt, searchDto.getUpdatedFrom(), searchDto.getUpdatedTo()));
        // 삭제 날짜 범위 검색
        builder.and(dateSearch(ai.deletedAt, searchDto.getDeletedFrom(), searchDto.getDeletedTo()));
        // 삭제 여부 조회 (기본값 false)
        if (searchDto.getIsDeleted() == null || !searchDto.getIsDeleted()) {
            builder.and(ai.isDeleted.eq(false));
        }

        // ai log id
        if (searchDto.getId() != null) {
            builder.and(ai.id.eq(searchDto.getId()));
        }

        // 메뉴 ID
        if (searchDto.getMenuId() != null) {
            builder.and(ai.menu.id.eq(searchDto.getMenuId()));
        }

        // 음식점 ID
        if (searchDto.getRestaurantId() != null) {
            builder.and(ai.menu.restaurant.id.eq(searchDto.getRestaurantId()));
        }

        // 음식점 이름
        if (searchDto.getRestaurantName() != null && !searchDto.getRestaurantName().isEmpty()) {
            builder.and(ai.menu.restaurant.name.eq(searchDto.getRestaurantName()));
        }

        // 요청 문구
        if (searchDto.getRequest() != null && !searchDto.getRequest().isEmpty()) {
            builder.and(ai.request.eq(searchDto.getRequest()));
        }

        // 응답 문구
        if (searchDto.getResponse() != null && !searchDto.getResponse().isEmpty()) {
            builder.and(ai.response.eq(searchDto.getResponse()));
        }

        // ai 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<AiLogResponseDto> content = queryFactory
            .select(Projections.constructor(
                AiLogResponseDto.class,
                ai.id,
                ai.menu.restaurant,
                ai.menu.id,
                ai.request,
                ai.response,
                ai.createdAt,
                ai.createdBy,
                ai.updatedAt,
                ai.updatedBy,
                ai.isDeleted,
                ai.deletedAt,
                ai.deletedBy
            ))
            .from(ai)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(ai.id)
            .from(ai)
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
