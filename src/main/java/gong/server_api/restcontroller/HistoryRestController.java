package gong.server_api.restcontroller;

import gong.server_api.domain.dto.HistoryDto;
import gong.server_api.domain.dto.HistoryInfoDto;
import gong.server_api.repository.UserRepository;
import gong.server_api.service.HistoryService;
import gong.server_api.service.UserService;
import groovy.util.logging.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class HistoryRestController {

    private final HistoryService historyService;
    private final UserRepository userRepository;
    private final UserService userService;

    public HistoryRestController(HistoryService historyService, UserRepository userRepository, UserService userService) {
        this.historyService = historyService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/history")
    public List<HistoryDto> getAllHistories() {
        try {
            // 인증된 사용자의 정보를 가져옵니다.
            String mail = userService.getCurrentUserEmail();

            // 사용자 이메일을 기반으로 해당 사용자의 history를 조회합니다.
            return historyService.findHistory(mail);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve histories");
        }
    }

    @GetMapping("/history/{historyId}")
    public List<HistoryInfoDto> getHistoryInfosByHistoryId(@PathVariable(name = "historyId") Integer historyId) {
        try {
            // 특정 history에 대한 정보들을 조회합니다.
            return historyService.findHistoryInfosByHistoryId(historyId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve history infos for historyId: " + historyId);
        }
    }
  /*\
  저
    @PostMapping("/history")
    public HistoryDto createHistory(@RequestBody HistoryDto historyDto) {
        try {
            // 새로운 history를 저장합니다.
            return historyService.saveHistory(historyDto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create history");
        }
    }
*/

    @PostMapping("/history/{historyId}")
    public HistoryInfoDto createHistoryInfo(@PathVariable(name = "historyId") Integer historyId, @RequestBody HistoryInfoDto historyInfoDto) {
        try {
            // 특정 history에 대한 정보를 저장합니다.
            return historyService.saveHistoryInfo(historyId, historyInfoDto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create history info for historyId: " + historyId);
        }
    }
    // 사용자 이메일을 가져오는 메서드

}