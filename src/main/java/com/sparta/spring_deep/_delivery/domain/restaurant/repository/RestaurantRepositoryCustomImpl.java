package com.sparta.spring_deep._delivery.domain.restaurant.repository;

import static com.sparta.spring_deep._delivery.domain.restaurant.entity.QRestaurant.restaurant;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.address.Address;
import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

  private JPAQueryFactory queryFactory;

  @Override
  public Page<Restaurant> findBySearchOption(Pageable pageable, UUID id, String restaurantName,
      Category category) {
    List<Restaurant> restaurants = queryFactory
        .selectFrom(restaurant)
        .where(restaurant.id.eq(id))
        .where(restaurant.name.eq(restaurantName))
        .where(restaurant.category.eq(category))
        .orderBy(restaurant.createdAt.asc())
        .fetch();
  }

  @Override
  public Page<Restaurant> findAllMember() {
    return null;
  }

  private BooleanExpression eqId(UUID id) {
    if (id == null) {
      return null;
    }
    return null;
  }
}
