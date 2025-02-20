package com.sparta.spring_deep._delivery.domain.category;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 검색
    @GetMapping("/search")
    public ResponseEntity<Page<CategoryResponseDto>> searchCategories(
        @RequestParam(required = false) UUID id,
        @RequestParam(required = false) String categoryName,
        @RequestParam(required = false, defaultValue = "true") boolean isAsc,
        @RequestParam(required = false, defaultValue = "updatedAt") String sortBy
    ) {

        Page<CategoryResponseDto> categoryResponseDtos = categoryService.searchCategories(id,
            categoryName, isAsc, sortBy);

        return ResponseEntity.status(HttpStatus.OK).body(categoryResponseDtos);
    }

    // 카테고리 추가
    @PostMapping()
    public ResponseEntity<CategoryResponseDto> createCategory(
        @RequestBody CategoryRequestDto categoryRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CategoryResponseDto categoryResponseDto = categoryService.createCategory(
            categoryRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
    }

    // 카테고리 업데이트
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @PathVariable UUID categoryId,
        @RequestBody CategoryRequestDto categoryRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CategoryResponseDto categoryResponseDto = categoryService.updateCategory(
            categoryId, categoryRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(categoryResponseDto);
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> deleteCategory(
        @PathVariable UUID categoryId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CategoryResponseDto categoryResponseDto = categoryService.deleteCategory(categoryId,
            userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(categoryResponseDto);
    }

}
