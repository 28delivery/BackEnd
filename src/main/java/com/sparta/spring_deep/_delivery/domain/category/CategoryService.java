package com.sparta.spring_deep._delivery.domain.category;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "카테고리 서비스")
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public Page<CategoryResponseDto> searchCategories(UUID id, String categoryName, boolean isAsc,
        String sortBy) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(0, 10, sort);
        List<Category> categories = new ArrayList<>();

        if (id == null && categoryName == null) {
            categories = categoryRepository.findAll();
        } else {
            if (categoryName == null) {
                categories.add(categoryRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 id 입니다.")
                ));
            } else if (id == null) {
                categories.add(categoryRepository.findByName(categoryName).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 카테고리 이름입니다.")
                ));
            } else {
                categories.add(categoryRepository.findByNameAndId(categoryName, id).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 id 혹은 카테고리 이름입니다.")
                ));
            }
        }

        return new PageImpl<>(categories, pageable, categories.size())
            .map(CategoryResponseDto::new);
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto,
        UserDetailsImpl userDetails) {
        // 토큰으로 사용자 정보 불러오기
        log.info("사용자 정보 토큰으로부터 가져오기");
        User loggedInUser = userDetails.getUser();

        log.info("카테고리 생성{}", categoryRequestDto.getName());
        Category category = new Category(categoryRequestDto, loggedInUser.getUsername());
        categoryRepository.save(category);

        return new CategoryResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID categoryId,
        CategoryRequestDto categoryRequestDto, UserDetailsImpl userDetails) {
        // 토큰으로 사용자 정보 불러오기
        User loggedInUser = userDetails.getUser();

        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 카테고리 id 입니다.")
        );

        category.updateCategory(categoryRequestDto, loggedInUser.getUsername());
        return new CategoryResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto deleteCategory(UUID categoryId, UserDetailsImpl userDetails) {
        // 토큰으로 사용자 정보 불러오기
        User loggedInUser = userDetails.getUser();

        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 카테고리 id 입니다.")
        );

        category.deleteCategory(loggedInUser.getUsername());
        return new CategoryResponseDto(category);
    }
}
