package com.sparta.spring_deep._delivery.domain.restaurantAddress;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantAddressService {

    private final RestaurantAddressRepository repository;

    @Transactional
    public RestaurantAddressResponseDto create(RestaurantAddressRequestDto dto) {

        RestaurantAddress address = new RestaurantAddress();

        RestaurantAddress saved = repository.save(address);
        return convertToResponseDto(saved);
    }

    @Transactional
    public RestaurantAddressResponseDto update(UUID id, RestaurantAddressRequestDto dto) {
        RestaurantAddress address = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("RestaurantAddress not found with id: " + id));

        address.setRoadAddr(dto.getRoadAddr());
        address.setRoadAddrPart1(dto.getRoadAddrPart1());
        address.setRoadAddrPart2(dto.getRoadAddrPart2());
        address.setJibunAddr(dto.getJibunAddr());
        address.setDetailAddr(dto.getDetailAddr());
        address.setEngAddr(dto.getEngAddr());
        address.setZipNo(dto.getZipNo());
        address.setSiNm(dto.getSiNm());
        address.setSggNm(dto.getSggNm());
        address.setEmdNm(dto.getEmdNm());
        address.setLiNm(dto.getLiNm());
        address.setRn(dto.getRn());
        address.setUdrtYn(dto.getUdrtYn());
        address.setBuldMnnm(dto.getBuldMnnm());
        address.setBuldSlno(dto.getBuldSlno());

        RestaurantAddress updated = repository.save(address);
        return convertToResponseDto(updated);
    }

    @Transactional(readOnly = true)
    public RestaurantAddressResponseDto getById(UUID id) {
        RestaurantAddress address = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("RestaurantAddress not found with id: " + id));
        return convertToResponseDto(address);
    }

    @Transactional(readOnly = true)
    public List<RestaurantAddressResponseDto> getAll() {
        return repository.findAll().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        RestaurantAddress address = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("RestaurantAddress not found with id: " + id));
        repository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<RestaurantAddressResponseDto> searchByRoadAddr(String roadAddr) {
        return repository.findByRoadAddrContaining(roadAddr)
            .stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    private RestaurantAddressResponseDto convertToResponseDto(RestaurantAddress address) {
        RestaurantAddressResponseDto dto = new RestaurantAddressResponseDto();
        dto.setId(address.getId());
        dto.setRoadAddr(address.getRoadAddr());
        dto.setRoadAddrPart1(address.getRoadAddrPart1());
        dto.setRoadAddrPart2(address.getRoadAddrPart2());
        dto.setJibunAddr(address.getJibunAddr());
        dto.setDetailAddr(address.getDetailAddr());
        dto.setEngAddr(address.getEngAddr());
        dto.setZipNo(address.getZipNo());
        dto.setSiNm(address.getSiNm());
        dto.setSggNm(address.getSggNm());
        dto.setEmdNm(address.getEmdNm());
        dto.setLiNm(address.getLiNm());
        dto.setRn(address.getRn());
        dto.setUdrtYn(address.getUdrtYn());
        dto.setBuldMnnm(address.getBuldMnnm());
        dto.setBuldSlno(address.getBuldSlno());
        return dto;
    }
}
