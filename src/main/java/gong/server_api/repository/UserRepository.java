package gong.server_api.repository;

import gong.server_api.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByHpid(String hpid);

    Boolean existsByUsername(String username);

    //username을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    User findByUsername(String username);
    // 기타 사용자 관련 커스텀 메서드 추가 가능

    User findByEmail(String email);
}