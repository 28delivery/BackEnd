package com.sparta.spring_deep._delivery.domain.menu;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class MenuRequestDto {

    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isHidden;

}
