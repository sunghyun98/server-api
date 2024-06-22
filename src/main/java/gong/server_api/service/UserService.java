package gong.server_api.service;

import gong.server_api.domain.dto.UserJoinDto;
import gong.server_api.domain.entity.user.Role;
import gong.server_api.domain.entity.user.User;
import gong.server_api.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void userJoin(UserJoinDto userJoinDto) {
        String email = userJoinDto.getEmail();

        // 이미 등록된 이메일인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

    // User 엔티티 생성
        User.UserBuilder userBuilder = User.builder()
                .username(userJoinDto.getUsername())
                .email(email)
                .password(bCryptPasswordEncoder.encode(userJoinDto.getPassword()))
                .hpid(userJoinDto.getHpid())
                .organization_name(userJoinDto.getOrganizationName());

        // 선택적 필드 설정
        if (userJoinDto.getRole() != null) {
            userBuilder.role(userJoinDto.getRole());
        } else {
            userBuilder.role(Role.USER); // 기본값은 일반 사용자
        }

        if (userJoinDto.getPhoneNumber() != null) {
            userBuilder.phone_number(userJoinDto.getPhoneNumber());
        }

        if (userJoinDto.getCreatedAt() != null) {
            userBuilder.created_at(userJoinDto.getCreatedAt());
        } else {
            userBuilder.created_at(LocalDateTime.now());
        }

        if (userJoinDto.getUpdatedAt() != null) {
            userBuilder.created_at(userJoinDto.getUpdatedAt());
        } else {
            userBuilder.created_at(LocalDateTime.now());
        }

        User user = userBuilder.build();

        // 사용자 저장
        userRepository.save(user);
    }
}



