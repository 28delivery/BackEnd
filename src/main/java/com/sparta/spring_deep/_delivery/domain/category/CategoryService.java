package com.sparta.spring_deep._delivery.domain.category;

import com.sparta.spring_deep._delivery.domain.user.User;
import com.sparta.spring_deep._delivery.domain.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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

    // TODO: 사용자 인증 정보로 User 불러오기
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        //임시방편
        User createUser = userRepository.findById("admin").orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Category category = new Category(categoryRequestDto, createUser.getUsername());
        return new CategoryResponseDto(category);
    }

    // TODO: 사용자 인증 정보로 User 불러오기
    @Transactional
    public CategoryResponseDto updateCategory(UUID categoryId,
        CategoryRequestDto categoryRequestDto) {
        //임시방편
        User updateUser = userRepository.findById("admin").orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 카테고리 id 입니다.")
        );

        category.updateCategory(categoryRequestDto, updateUser.getUsername());
        return new CategoryResponseDto(category);
    }

    // TODO: 사용자 인증 정보로 User 불러오기
    @Transactional
    public CategoryResponseDto deleteCategory(UUID categoryId) {
        //임시방편
        User deleteUser = userRepository.findById("admin").orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 카테고리 id 입니다.")
        );

        category.deleteCategory(deleteUser.getUsername());
        return new CategoryResponseDto(category);
    }
}
