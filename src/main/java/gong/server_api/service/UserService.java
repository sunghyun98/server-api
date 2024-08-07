package gong.server_api.service;

import gong.server_api.domain.dto.UserJoinDto;
import gong.server_api.domain.dto.UserDto;
import gong.server_api.domain.entity.user.Role;
import gong.server_api.domain.entity.user.User;
import gong.server_api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Slf4j
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
        User user = User.builder()
                .username(userJoinDto.getUsername())
                .email(email)
                .password(bCryptPasswordEncoder.encode(userJoinDto.getPassword()))
                .hpid(userJoinDto.getHpid())
                .organization_name(userJoinDto.getOrganizationName())
                .role(userJoinDto.getRole() != null ? userJoinDto.getRole() : Role.USER) // 기본값 설정
                .phone_number(userJoinDto.getPhoneNumber())
                .created_at(userJoinDto.getCreatedAt() != null ? userJoinDto.getCreatedAt() : LocalDateTime.now())
                .updated_at(userJoinDto.getUpdatedAt() != null ? userJoinDto.getUpdatedAt() : LocalDateTime.now())
                .build();

        // 사용자 저장
        userRepository.save(user);
    }

    // HPID로 유저 정보 찾기
// HPID로 유저 정보 찾기
    public UserDto findUserInfo() {
        // 인증된 사용자의 정보를 가져옵니다.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mail = userDetails.getUsername();

        // 이메일로 유저 정보를 조회합니다.
        Optional<User> optionalUser = userRepository.findByEmail(mail);

        // 유저가 존재하지 않으면 예외를 던집니다.
        User user = optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found for email: " + mail));

        // User 객체를 UserDto로 변환합니다.
        return new UserDto(
                user.getEmail(),
                user.getUsername(),
                user.getPhone_number(),
                user.getOrganization_name(),
                user.getHpid(),
                user.getRole().name()
        );
    }
    public Optional<User> findUserByMail(String mail) {
        log.info("findUserByMail");
        return userRepository.findByEmail(mail);
    }

    public String getCurrentUserEmail() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
