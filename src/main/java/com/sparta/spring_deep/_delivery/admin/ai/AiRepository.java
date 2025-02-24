package com.sparta.spring_deep._delivery.admin.ai;

import com.sparta.spring_deep._delivery.domain.ai.Ai;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID>, AiRepositoryCustom {

}
