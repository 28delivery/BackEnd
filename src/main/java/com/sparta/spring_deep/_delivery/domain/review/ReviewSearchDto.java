package com.sparta.spring_deep._delivery.domain.review;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReviewSearchDto {

    private String username;
    private Integer rating;
    private String comment;

}
