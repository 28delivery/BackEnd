package com.sparta.spring_deep._delivery.domain.menu;

import static com.sparta.spring_deep._delivery.domain.menu.QMenu.menu;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class MenuRepositoryCustomImpl implements MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MenuRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Menu> searchMenusByRestaurantId(UUID restaurantUUID, String menuName,
        Pageable pageable) {

        // 기본 조건: 삭제되지 않은 메뉴만 조회
        BooleanExpression baseCondition = menu.isDeleted.eq(false)
            .and(menu.restaurant.id.eq(restaurantUUID));

        // 메뉴 이름이 들어온 경우 필터링
        if (menuName != null) {
            baseCondition = baseCondition.and(menu.name.eq(menuName));
        }

        // 메뉴 조회 쿼리
        JPAQuery<Menu> query = queryFactory
            .selectFrom(menu)
            .where(baseCondition);

        // 페이징 적용 후 데이터 조회
        List<Menu> menus = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 전체 개수 조회
        Long total = queryFactory
            .select(menu.count())
            .from(menu)
            .where(baseCondition)
            .fetchOne();

        return new PageImpl<>(menus, pageable, total != null ? total : 0);
    }
}
