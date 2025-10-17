/*package io.goorm.team02.core.users.service;

import io.goorm.team02.core.auth.security.SecurityUtils;
import io.goorm.team02.core.users.controller.dto.UserAddressRequest;
import io.goorm.team02.core.users.controller.dto.UserAddressResponse;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.repository.UserAddressRepository;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.goorm.team02.core.users.domain.User;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository addressRepository;
    private final UserinfoRepository userRepository;

    // 1. 새로운 주소 등록
    @Transactional
    public UserAddressResponse addAddress(Long userId,UserAddressRequest request) {
        // userid로 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // useraddress객체 생성 
        UserAddress address = new UserAddress();
        address.setUser(user);// JWT에서 PK 가져오기
        address.setAddressName(request.getAddressName());
        address.setAddress(request.getAddress());
        address.setDetailAddress(request.getDetailAddress());
        address.setZipcode(request.getZipcode());
        address.setIsDefault(false); // 기본값 false

        //return addressRepository.save(address);// 엔티티(UserAddress 를 그대로 들고있어서 무한루프
        UserAddress saved = addressRepository.save(address);
        
        // ✅ DTO로 변환해서 반환
        return new UserAddressResponse(
                saved.getId(),
                saved.getAddressName(),
                saved.getAddress(),
                saved.getDetailAddress(),
                saved.getZipcode(),
                saved.getIsDefault()
            );
       }
    }*/