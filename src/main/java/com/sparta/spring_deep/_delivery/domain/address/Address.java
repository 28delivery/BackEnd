package com.sparta.spring_deep._delivery.domain.address;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import com.sparta.spring_deep._delivery.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_address")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "username")
    @NotNull
    private User user;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String address;

    @NotNull
    @Column(name = "address_name")
    private String address_name;

    public Address(AddressRequestDto requestDto, User user) {
        super(user);
        this.user = user;
        this.address = requestDto.getAddress();
        this.address_name = requestDto.getAddress_name();
    }

    public void updateAddress(AddressRequestDto requestDto) {
        this.address = requestDto.getAddress();
        this.address_name = requestDto.getAddress_name();
    }

}
