package com.sparta.spring_deep._delivery.domain.ai;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.menu.Menu;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "p_ai_log")
public class Ai extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private String request;

    @Column(nullable = false)
    private String response;

    public Ai(
        Menu menu,
        String request,
        String response,
        User user
    ) {
        super(user.getUsername());
        this.menu = menu;
        this.request = request;
        this.response = response;
    }
}
