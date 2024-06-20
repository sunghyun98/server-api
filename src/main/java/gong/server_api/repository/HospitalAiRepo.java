package gong.server_api.repository;

import gong.server_api.domain.entity.user.HospitalAi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalAiRepo extends JpaRepository<HospitalAi, Integer> {
    Optional<HospitalAi> findByHpid(String hpid);
}
