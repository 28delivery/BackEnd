package com.sparta.spring_deep._delivery.admin.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiLogService aiLogService;

    @GetMapping("/admin/logs/ai/search")
    public ResponseEntity<Page<AiLogResponseDto>> searchAiLogs(
        @ModelAttribute AiLogSearchDto aiLogSearchDto,
        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AiLogResponseDto> logs = aiLogService.searchAiLogs(aiLogSearchDto, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(logs);
    }

}
