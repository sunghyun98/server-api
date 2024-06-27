package gong.server_api.repository;

import gong.server_api.domain.entity.user.HospitalAi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HospitalAiRepo extends JpaRepository<HospitalAi, Integer> {
    Optional<HospitalAi> findByHpid(String hpid);

    @Query("SELECT h.dgidIdName FROM HospitalAi h WHERE h.hpid = :hpid")
    String findDgidIdNameByHpid(@Param("hpid") String hpid);
}
