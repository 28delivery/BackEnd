package com.sparta.spring_deep._delivery.domain.menu;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequestDto {

    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isHidden;

}
