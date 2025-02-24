package com.sparta.spring_deep._delivery.domain.user.repository;

import com.sparta.spring_deep._delivery.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {

    Optional<User> findByUsername(String username);
    
    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}
