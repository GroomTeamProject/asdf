package io.goorm.team02.core.users.service;

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
<<<<<<< HEAD
    }


    // 2. 등록된 주소 조회
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAllAddresses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return addressRepository.findByUser(user)
                .stream()
                .map(address -> new UserAddressResponse(
                        address.getId(),
                        address.getAddressName(),
                        address.getAddress(),
                        address.getDetailAddress(),
                        address.getZipcode(),
                        address.getIsDefault()
                ))
                .toList();
    }

    // 3. 특정 주소 수정
    @Transactional
    public UserAddressResponse updateAddress(Long userId, Long addressId, UserAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 현재 로그인한 유저인지 확인
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 주소만 수정할 수 있습니다.");
        }

        // 수정 가능한 필드 업데이트
        address.setAddressName(request.getAddressName());
        address.setAddress(request.getAddress());
        address.setDetailAddress(request.getDetailAddress());
        address.setZipcode(request.getZipcode());
        address.setIsDefault(request.getIsDefault());

        // isDefault 처리: true로 수정한 경우 다른 주소를 false로 변경
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUser(user).forEach(a -> {
                if (!a.getId().equals(addressId)) {
                    a.setIsDefault(false);
                }
            });
        }

        addressRepository.save(address);

        return new UserAddressResponse(
                address.getId(),
                address.getAddressName(),
                address.getAddress(),
                address.getDetailAddress(),
                address.getZipcode(),
                address.getIsDefault()
        );
    }

    // 4. 주소 삭제 
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 본인 주소인지 확인
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 주소만 삭제할 수 있습니다.");
        }

        addressRepository.delete(address);

        // 기본주소(true) 삭제시 처리
        // 삭제한 주소가 기본 주소였으면, 다른 주소 중 하나를 기본으로 설정 (옵션)
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.findByUser(user).stream().findFirst()
                .ifPresent(a -> a.setIsDefault(true));
        }
    }


}
=======
       }
    }
>>>>>>> develop
