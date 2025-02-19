package com.sparta.spring_deep._delivery.domain.restaurantAddress;

import com.sparta.spring_deep._delivery.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Table(name = "p_restaurant_address")
public class RestaurantAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    private UUID id;

    @Column(name = "road_addr", length = 100, nullable = false)
    private String roadAddr;

    @Column(name = "road_addr_part1", length = 100, nullable = false)
    private String roadAddrPart1;

    @Column(name = "road_addr_part2", length = 100, nullable = false)
    private String roadAddrPart2;

    @Column(name = "jibun_addr", length = 100, nullable = false)
    private String jibunAddr;

    @Column(name = "detail_addr", length = 100, nullable = false)
    private String detailAddr;

    @Column(name = "eng_addr", length = 100, nullable = false)
    private String engAddr;

    @Column(name = "zip_no", length = 50, nullable = false)
    private String zipNo;

    @Column(name = "si_nm", length = 50, nullable = false)
    private String siNm;

    @Column(name = "sgg_nm", length = 50)
    private String sggNm;

    @Column(name = "emd_nm", length = 50, nullable = false)
    private String emdNm;

    @Column(name = "li_nm", length = 50)
    private String liNm;

    @Column(name = "rn", length = 50, nullable = false)
    private String rn;

    @Column(name = "udrt_yn", length = 50, nullable = false)
    private String udrtYn;

    @Column(name = "buld_mnnm", length = 50, nullable = false)
    private String buldMnnm;

    @Column(name = "buld_slno", length = 50, nullable = false)
    private String buldSlno;

    public RestaurantAddress(RestaurantAddressRequestDto dto) {
    }
}




