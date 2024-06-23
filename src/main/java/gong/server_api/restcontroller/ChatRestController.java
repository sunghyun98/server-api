package gong.server_api.restcontroller;

import gong.server_api.domain.dto.chat.ChatMessage;
import gong.server_api.domain.dto.chat.HospitalChatMessage;
import gong.server_api.service.ChatService;
import gong.server_api.service.HospitalConnectionStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatRestController {
    private final ChatService chatService;
    private final HospitalConnectionStatusService hospitalConnectionStatusService;

    public ChatRestController(ChatService chatService, HospitalConnectionStatusService hospitalConnectionStatusService) {
        this.chatService = chatService;
        this.hospitalConnectionStatusService = hospitalConnectionStatusService;
    }

    @GetMapping("/received/{hpid}")
    public List<ChatMessage> getReceivedChat(@PathVariable(name = "hpid") String hpid){
        return chatService.findReceivedChatList(hpid);
    }

    @GetMapping("/hospital/{hpid}")
    public List<HospitalChatMessage> getHospitalChat(@PathVariable(name = "hpid") String hpid){
        return chatService.findHospitalChat(hpid);
    }


    @GetMapping("/connected")
    public List<String> getConnectedHospitals() {
        return hospitalConnectionStatusService.getConnectedHospitals();
    }
}
