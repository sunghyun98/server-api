package gong.server_api.service;

import gong.server_api.domain.dto.HistoryDto;
import gong.server_api.domain.dto.HistoryInfoDto;
import gong.server_api.domain.entity.History;
import gong.server_api.domain.entity.HistoryInfo;
import gong.server_api.repository.HistoryRepository;
import gong.server_api.repository.HistoryInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryInfoRepository historyInfoRepository;

    public HistoryService(HistoryRepository historyRepository, HistoryInfoRepository historyInfoRepository) {
        this.historyRepository = historyRepository;
        this.historyInfoRepository = historyInfoRepository;
    }

    // 내 히스토리 리스트 출력
    public List<HistoryDto> findHistory(String userMail) {
        return historyRepository.findByUsermail(userMail).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 히스토리 상세조회
    public List<HistoryInfoDto> findHistoryInfosByHistoryId(Integer historyId) {
        return historyInfoRepository.findByHistoryHistoryId(historyId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 새로운 히스토리 추가
    public HistoryDto saveHistory(HistoryDto historyDto) {

        History history = new History();
        history.setDate(historyDto.getDate());
        history.setDgidIdName(historyDto.getDgidIdName());
        history.setDutyName(historyDto.getDutyName());
        history.setHpid(historyDto.getHpid());
        history.setProfileImage(historyDto.getProfileImage());
        history.setUsermail(historyDto.getUsermail());
        history.setType(historyDto.getType());

        History savedHistory = historyRepository.save(history);

        return convertToDto(savedHistory);
    }

    // 히스토리 정보 추가
    public HistoryInfoDto saveHistoryInfo(Integer historyId, HistoryInfoDto historyInfoDto) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid historyId: " + historyId));
        HistoryInfo historyInfo = convertToEntity(historyInfoDto);
        historyInfo.setHistory(history);
        HistoryInfo savedHistoryInfo = historyInfoRepository.save(historyInfo);
        return convertToDto(savedHistoryInfo);
    }

    // DTO -> Entity 변환
    private History convertToEntity(HistoryDto historyDto) {
        History history = new History();
        history.setDate(historyDto.getDate());
        history.setDgidIdName(historyDto.getDgidIdName());
        history.setDutyName(historyDto.getDutyName());
        history.setHpid(historyDto.getHpid());
        history.setProfileImage(historyDto.getProfileImage());
        history.setUsermail(historyDto.getUsermail());
        history.setType(historyDto.getType());
        return history;
    }

    private HistoryInfo convertToEntity(HistoryInfoDto historyInfoDto) {
        HistoryInfo historyInfo = new HistoryInfo();
        historyInfo.setTitle(historyInfoDto.getTitle());
        historyInfo.setContent(historyInfoDto.getContent());
        historyInfo.setDate(historyInfoDto.getDate());
        return historyInfo;
    }

    // Entity -> DTO 변환
    private HistoryDto convertToDto(History history) {
        HistoryDto historyDto = new HistoryDto();
        historyDto.setHistoryId(history.getHistoryId());
        historyDto.setDate(history.getDate());
        historyDto.setDgidIdName(history.getDgidIdName());
        historyDto.setDutyName(history.getDutyName());
        historyDto.setHpid(history.getHpid());
        historyDto.setProfileImage(history.getProfileImage());
        historyDto.setUsermail(history.getUsermail());
        historyDto.setType(history.getType());
        return historyDto;
    }

    private HistoryInfoDto convertToDto(HistoryInfo historyInfo) {
        HistoryInfoDto historyInfoDto = new HistoryInfoDto();
        historyInfoDto.setId(historyInfo.getId());
        historyInfoDto.setHistoryId(historyInfo.getHistory().getHistoryId());
        historyInfoDto.setTitle(historyInfo.getTitle());
        historyInfoDto.setContent(historyInfo.getContent());
        historyInfoDto.setDate(historyInfo.getDate());
        return historyInfoDto;
    }
}