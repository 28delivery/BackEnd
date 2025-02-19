package com.sparta.spring_deep._delivery.domain.category;

import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CategoryRequestDto {

    private UUID id;
    private String name;

}
