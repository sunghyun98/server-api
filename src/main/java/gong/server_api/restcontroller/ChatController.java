package gong.server_api.restcontroller;

import gong.server_api.domain.dto.chat.Chat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    // 클라이언트가 "/api/chat/sendMessage"로 POST 요청을 보낼 때 호출됩니다.
    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody Chat chat) {
        // 여기에 메시지 전송 로직을 추가합니다.
        // messagingTemplate.convertAndSend("/topic/chat/" + chatMessageDto.getChatRoomId(), chatMessageDto);

        // 응답을 보냅니다.
        return ResponseEntity.status(HttpStatus.OK).body("Message sent successfully");
    }

    // 클라이언트가 "/api/chat/addUser"로 POST 요청을 보낼 때 호출됩니다.
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody Chat chat) {
        // 여기에 사용자 추가 로직을 추가합니다.
        // headerAccessor.getSessionAttributes().put("username", chatMessageDto.getSenderId());
        // messagingTemplate.convertAndSend("/topic/chat/" + chatMessageDto.getChatRoomId(), chatMessageDto);

        // 응답을 보냅니다.
        return ResponseEntity.status(HttpStatus.OK).body("User added successfully");
    }
}