package gong.server_api.chat;

import gong.server_api.service.ChatService;
import gong.server_api.service.HospitalService;
import gong.server_api.service.PythonService;

public class ChatController {
    private final ChatService chatService;
    private final PythonService pythonService;
    private final HospitalService hospitalService;
    public ChatController(ChatService chatService, PythonService pythonService, HospitalService hospitalService) {
        this.chatService = chatService;
        this.pythonService = pythonService;
        this.hospitalService = hospitalService;
    }


}
