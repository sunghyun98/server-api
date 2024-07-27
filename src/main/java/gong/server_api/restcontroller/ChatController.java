package gong.server_api.restcontroller;

import gong.server_api.CustomUserDetails;
import gong.server_api.domain.dto.ChattingRoomDto;
import gong.server_api.domain.dto.LocationDto;
import gong.server_api.domain.entity.user.Role;
import gong.server_api.service.ChatService;
import gong.server_api.service.HospitalService;
import gong.server_api.service.PythonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;
    private final PythonService pythonService;
    private final HospitalService hospitalService;
    public ChatController(ChatService chatService, PythonService pythonService, HospitalService hospitalService) {
        this.chatService = chatService;
        this.pythonService = pythonService;
        this.hospitalService = hospitalService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody ChattingRoomDto chattingRoomDto) {
        log.info("createRoom");
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        chattingRoomDto.setSenderId(userDetails.getUsername());

        ChattingRoomDto createdRoom = chatService.createHospitalRoom(chattingRoomDto);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("roomId", createdRoom.getId());
        response.put("roomName", createdRoom.getRoomName());

        return ResponseEntity.ok(response);
    }
}
