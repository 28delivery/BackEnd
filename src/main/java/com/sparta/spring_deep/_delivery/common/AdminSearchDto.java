package com.sparta.spring_deep._delivery.common;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AdminSearchDto {

    private Boolean isDeleted;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private String createdBy;
    private LocalDateTime updatedFrom;
    private LocalDateTime updatedTo;
    private String updatedBy;
    private LocalDateTime deletedFrom;
    private LocalDateTime deletedTo;
    private String deletedBy;

}
