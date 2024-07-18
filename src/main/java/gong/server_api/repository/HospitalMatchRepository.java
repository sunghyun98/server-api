package gong.server_api.repository;

import gong.server_api.domain.entity.HospitalMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalMatchRepository extends JpaRepository<HospitalMatch, Long> {
    List<HospitalMatch> findByUserMail(String userMail);
}
