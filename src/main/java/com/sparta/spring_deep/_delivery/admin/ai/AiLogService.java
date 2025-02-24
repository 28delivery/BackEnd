package com.sparta.spring_deep._delivery.admin.ai;

import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AiLogService")
public class AiLogService {

    private final AiRepository aiRepository;

    // AI 로그 검색
    public Page<AiLogResponseDto> searchAiLogs(AiLogSearchDto aiLogSearchDto, Pageable pageable) {
        log.info("searchAiLogs");

        Page<AiLogResponseDto> aiLogs = aiRepository.searchByOption(aiLogSearchDto, pageable);

        // aiLog 검색 결과가 비어있다면 Exception 출력
        if (aiLogs.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return aiLogs;
    }
}
