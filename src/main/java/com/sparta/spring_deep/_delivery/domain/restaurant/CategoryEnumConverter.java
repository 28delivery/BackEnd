//package com.sparta.spring_deep._delivery.domain.restaurant;
//
//import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import java.util.Arrays;
//
//@Converter(autoApply = true)
//public class CategoryEnumConverter implements AttributeConverter<CategoryEnum, String> {
//
//    @Override
//    public String convertToDatabaseColumn(Restaurant.CategoryEnum attribute) {
//        return attribute == null ? null : attribute.getLabel();
//    }
//
//    @Override
//    public Restaurant.CategoryEnum convertToEntityAttribute(String dbData) {
//        if (dbData == null) {
//            return null;
//        }
//        return Arrays.stream(Restaurant.CategoryEnum.values())
//            .filter(e -> e.getLabel().equals(dbData))
//            .findFirst()
//            .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + dbData));
//    }
//}
