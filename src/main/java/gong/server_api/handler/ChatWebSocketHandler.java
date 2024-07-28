
package gong.server_api.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import gong.server_api.domain.dto.*;
import gong.server_api.jwt.JWTUtil;
import gong.server_api.service.request.PythonRequest;
import gong.server_api.service.response.PythonResponse;
import gong.server_api.service.PythonService;

import java.net.URI;
import java.sql.Timestamp;

import gong.server_api.domain.entity.user.User;
import gong.server_api.repository.UserRepository;
import gong.server_api.service.ChatService;
import gong.server_api.service.HospitalConnectionStatusService;
import gong.server_api.service.HospitalService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final HospitalService hospitalService;
    private final ObjectMapper objectMapper;
    private final HospitalConnectionStatusService hospitalConnectionStatusService;
    private final PythonService pythonService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public ChatWebSocketHandler( HospitalService hospitalService, ObjectMapper objectMapper, HospitalConnectionStatusService hospitalConnectionStatusService, PythonService pythonService, UserRepository userRepository, JWTUtil jwtUtil) {

        this.hospitalService = hospitalService;
        this.objectMapper = objectMapper;
        this.hospitalConnectionStatusService = hospitalConnectionStatusService;
        this.pythonService = pythonService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            // Get email from session attributes
            String email = (String) session.getAttributes().get("email");
            String role = (String) session.getAttributes().get("role");
            String organization = (String) session.getAttributes().get("organization");
            log.info("User connected: email={}, role={}", email, role);

            // Update hospital connection status
            hospitalConnectionStatusService.updateConnectionStatus(email, role, true, organization);

            // Store the session using email as the key
            sessions.put(email, session);
            if ("FIREFIGHTER".equalsIgnoreCase(role)) {
                try {
                    session.sendMessage(new TextMessage("안녕하세요!\n증상을 입력해 주시면 가장 가까운 응급실을 찾아드리겠습니다."));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to send message to firefighter", e);
                }
            }

            log.info("User connected: email={}, hpid={}, role={}", email, email, role);
            log.info("User connected: " + email);

        } catch (Exception e) {
            log.error("Error during connection establishment", e);
            throw new RuntimeException("Error during connection establishment", e);
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            BaseMessageDto baseMessageDto = objectMapper.readValue(payload, BaseMessageDto.class);
            log.info("Received message: {}", baseMessageDto);

            String type = baseMessageDto.getType();
            if ("AI".equalsIgnoreCase(type)) {
                ChatBotMessageDto chatBotMessageDto = objectMapper.readValue(payload, ChatBotMessageDto.class);
                handleAiMessage(session, chatBotMessageDto);
            } else if ("CHAT".equalsIgnoreCase(type)) {
                ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
                handleChatMessage(session, chatMessageDto);
            } else {
                sendErrorMessage(session, "Unknown message type");
            }
        } catch (IOException e) {
            log.error("Error processing message", e);
            sendErrorMessage(session, "Invalid message format");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendErrorMessage(session, "An unexpected error occurred");
        }
    }

    private void handleAiMessage(WebSocketSession session, ChatBotMessageDto chatMessageDto) {
        try {
            chatMessageDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            PythonRequest pythonRequest = new PythonRequest();
            pythonRequest.setSituation(chatMessageDto.getContent());
            pythonRequest.setLatitude(Double.parseDouble(chatMessageDto.getLatitude()));
            pythonRequest.setLongitude(Double.parseDouble(chatMessageDto.getLongitude()));

            PythonResponse pythonResponse = pythonService.executePythonScript(pythonRequest);
            log.info("pythonResponse={}", pythonResponse);

            double latitude = pythonRequest.getLatitude();
            double longitude = pythonRequest.getLongitude();
            String email = (String) session.getAttributes().get("email");

            List<HospitalCombinedDto> hospitalAddress = hospitalService.getHospitalAddress(latitude, longitude, pythonResponse, email);
            String safetyMessage = pythonResponse.getSafetyMessage();
            log.info("safetyMessage={}", safetyMessage);

            Map<String, Object> response = new HashMap<>();
            response.put("safetyMessage", safetyMessage);
            response.put("hospital", hospitalAddress);
            response.put("hospitalCount", hospitalAddress.size());
            log.info("hospitalAddress={}", hospitalAddress);

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));

        } catch (IOException e) {
            log.error("Error processing AI message", e);
            sendErrorMessage(session, "Error processing AI message");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendErrorMessage(session, "An unexpected error occurred");
        }
    }

    private void handleChatMessage(WebSocketSession session, ChatMessageDto chatMessageDto) {
        try {
            /*
            메시지 핸들러 로직 구현
            챗봇
            1단계
            - 소방관이 hpid로 메시지를 보내게 된다면 채팅방이 생성 채팅방 생성(채팅방 이름은 병원이름 + 날짜)
            - 채팅방 정보
                - sender receiver
                - 소속    소속
                -

            2단계 생성된 채팅방 id로 메시지를 주고 받음
            - json (roomId content)
            챗봇 병원 리스트 (hpid) <-> 소방차 (hpid)- 소방차 중복
             */

            // 보내는 사람
            String senderEmail = (String) session.getAttributes().get("email");
            chatMessageDto.setSenderUserId(senderEmail);

            // 받는 사람
            String receiverIdentifier = chatMessageDto.getReceiverUserId();
            WebSocketSession receiverSession = sessions.get(receiverIdentifier);

            // Check if the chat room already exists (message contains chat room ID)
            if (chatMessageDto.getChattingRoomId() != null) {
                receiverSession = sessions.get(receiverIdentifier); // Using email as key
            } else {
                // If chat room does not exist, find user by HPID
                Optional<User> receiverUserOptional = userRepository.findByHpid(receiverIdentifier);
                if (receiverUserOptional.isPresent()) {
                    User receiverUser = receiverUserOptional.get();
                    receiverSession = sessions.get(receiverUser.getEmail());
                } else {
                    log.warn("Receiver with HPID {} not found", receiverIdentifier);
                    sendErrorMessage(session, "Receiver not found");
                    return;
                }
            }
            // If receiver session is found and open, handle the message
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
                log.info("Message sent from {} to {}", chatMessageDto.getSenderUserId(), receiverIdentifier);
            } else {
                log.warn("Receiver {} is not connected", receiverIdentifier);
                sendErrorMessage(session, "Receiver not connected");
            }
        } catch (IOException e) {
            log.error("Error processing chat message", e);
            sendErrorMessage(session, "Error processing chat message");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendErrorMessage(session, "An unexpected error occurred");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            // Get email from session attributes
            String email = (String) session.getAttributes().get("email");
            String role = (String) session.getAttributes().get("role");
            String organization = (String) session.getAttributes().get("organization");
            log.info("User disconnected: email={}, role={}", email, role);

            // Update hospital connection status
            hospitalConnectionStatusService.updateConnectionStatus(email, false);

            sessions.remove(email);

            hospitalService.deleteHospitalMatchesByUserHpid(email);
            log.info("User disconnected: " + email);
        } catch (Exception e) {
            log.error("Error during connection closure", e);
        }
    }

    public void sendMessage(String senderId, String receiverId, Map<String, Object> message) {
        try {
            WebSocketSession senderSession = sessions.get(senderId);
            if (senderSession != null && senderSession.isOpen()) {
                WebSocketSession receiverSession = sessions.get(receiverId);
                if (receiverSession != null && receiverSession.isOpen()) {
                    receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                    log.info("Message sent from {} to {}", senderId, receiverId);
                } else {
                    log.warn("Receiver {} is not connected", receiverId);
                }
            } else {
                log.warn("Sender {} is not connected", senderId);
            }
        } catch (IOException e) {
            log.error("Error sending message from {} to {}", senderId, receiverId, e);
        }
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("error", errorMessage))));
        } catch (IOException e) {
            log.error("Error sending error message", e);
        }
    }
}