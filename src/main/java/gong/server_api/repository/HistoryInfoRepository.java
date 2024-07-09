package gong.server_api.repository;

import gong.server_api.domain.entity.HistoryInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryInfoRepository extends JpaRepository<HistoryInfo, Integer> {
    List<HistoryInfo> findByHistoryHistoryId(Integer historyId);
}