package gong.server_api.repository;

import gong.server_api.domain.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findByUsermail(String usermail);
}