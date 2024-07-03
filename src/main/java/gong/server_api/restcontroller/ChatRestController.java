package gong.server_api.restcontroller;

import gong.server_api.domain.dto.ChatMessageDto;
import gong.server_api.domain.dto.HospitalChatMessageDto;
import gong.server_api.handler.ChatWebSocketHandler;
import gong.server_api.service.ChatService;
import gong.server_api.service.HospitalConnectionStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/chat")
public class ChatRestController {
    private final ChatService chatService;
    private final HospitalConnectionStatusService hospitalConnectionStatusService;

    private final ChatWebSocketHandler chatWebSocketHandler;

    public ChatRestController(ChatService chatService, HospitalConnectionStatusService hospitalConnectionStatusService, ChatWebSocketHandler chatWebSocketHandler) {
        this.chatService = chatService;
        this.hospitalConnectionStatusService = hospitalConnectionStatusService;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @GetMapping("/received/{hpid}")
    public List<ChatMessageDto> getReceivedChat(@PathVariable(name = "hpid") String hpid){
        return chatService.findReceivedChatList(hpid);
    }

    @GetMapping("/hospital/{hpid}")
    public List<HospitalChatMessageDto> getHospitalChat(@PathVariable(name = "hpid") String hpid){
        return chatService.findHospitalChat(hpid);
    }


    @GetMapping("/connected")
    public List<String> getConnectedHospitals() {
        return hospitalConnectionStatusService.getConnectedHospitals();
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            // JSON에서 senderHpid와 r eceiverHpid 추출
            String senderHpid = (String) request.get("senderHpid");
            String receiverHpid = (String) request.get("receiverHpid");

            // 메시지 추출 (예를 들어, "message"라는 key로 메시지가 전송된다고 가정)
            String messageContent = (String) request.get("message");

            // 필요한 메시지 생성
            Map<String, Object> message = new HashMap<>();
            message.put("message", messageContent);

            // WebSocketHandler를 통해 메시지 전송
            chatWebSocketHandler.sendMessage(senderHpid, receiverHpid, message);

            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message: " + e.getMessage());
        }
    }
}
