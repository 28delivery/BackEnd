package com.sparta.spring_deep._delivery.domain.category;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "p_category")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID id;

    private String name;

    public Category(UUID uuid, String name) {
        this.id = uuid;
        this.name = name;
    }

    public Category(CategoryRequestDto categoryRequestDto, User createUser) {
        super(createUser);
        this.id = categoryRequestDto.getId();
        this.name = categoryRequestDto.getName();
    }

    public void updateCategory(CategoryRequestDto categoryRequestDto, User updateUser) {
        super.update(updateUser);
        this.id = categoryRequestDto.getId();
        this.name = categoryRequestDto.getName();
    }

    public void deleteCategory(User deleteUser) {
        super.delete(deleteUser);
    }
}
