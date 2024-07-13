
package gong.server_api.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import gong.server_api.domain.dto.BaseMessageDto;
import gong.server_api.domain.dto.ChatBotMessageDto;
import gong.server_api.service.request.PythonRequest;
import gong.server_api.service.response.PythonResponse;
import gong.server_api.service.PythonService;
import gong.server_api.domain.dto.ChatMessageDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import gong.server_api.domain.dto.HospitalCombinedDto;
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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final HospitalService hospitalService;
    private final ObjectMapper objectMapper;
    private final HospitalConnectionStatusService hospitalConnectionStatusService;
    private final PythonService pythonService;
    private final UserRepository userRepository;

    public ChatWebSocketHandler(ChatService chatService, HospitalService hospitalService, ObjectMapper objectMapper, HospitalConnectionStatusService hospitalConnectionStatusService, PythonService pythonService, UserRepository userRepository) {
        this.chatService = chatService;
        this.hospitalService = hospitalService;
        this.objectMapper = objectMapper;
        this.hospitalConnectionStatusService = hospitalConnectionStatusService;
        this.pythonService = pythonService;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            String hpid = getHpidFromSession(session);
            sessions.put(hpid, session);

            List<String> connectedHospitals = hospitalConnectionStatusService.getConnectedHospitals();
            String connectedHospitalsJson = new ObjectMapper().writeValueAsString(connectedHospitals);

            userRepository.findByHpid(hpid).ifPresent(user -> {
                String role = String.valueOf(user.getRole());
                if ("FIREFIGHTER".equalsIgnoreCase(role)) {
                    try {
                        session.sendMessage(new TextMessage("안녕하세요!\n증상을 입력해 주시면 가장 가까운 응급실을 찾아드리겠습니다."));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    List<String> firefighterList = new ArrayList<>();
                    for (String connectedHospital : connectedHospitals) {
                        userRepository.findByHpid(connectedHospital).ifPresent(hospitalUser -> {
                            if ("FIREFIGHTER".equalsIgnoreCase(String.valueOf(hospitalUser.getRole()))) {
                                firefighterList.add(hospitalUser.getOrganization_name());
                            }
                        });
                    }
                    try {
                        String firefighterListJson = new ObjectMapper().writeValueAsString(firefighterList);
                        session.sendMessage(new TextMessage("현재 연결된 소방관 목록: " + firefighterListJson));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("User connected: hpid={}, role={}", hpid, role);
            });
            hospitalConnectionStatusService.updateConnectionStatus(hpid, true);
            log.info("User connected: " + hpid);

        } catch (Exception e) {
            log.error("Error during connection establishment", e);
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
            log.info("latitude={}", getHpidFromSession(session));

            List<HospitalCombinedDto> hospitalAddress = hospitalService.getHospitalAddress(latitude, longitude, pythonResponse, getHpidFromSession(session));
            String safetyMessage = pythonResponse.getSafetyMessage();
            log.info("safetyMessage={}", safetyMessage);

            Map<String, Object> response = new HashMap<>();
            response.put("safetyMessage", safetyMessage);
            response.put("hospital", hospitalAddress);
            response.put("hospitalCount", hospitalAddress.size());
            log.info("hospitalAddress={}", hospitalAddress);

            WebSocketSession senderSession = sessions.get(getHpidFromSession(session));
            if (senderSession != null && senderSession.isOpen()) {
                senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                log.info("Hospital list sent to {}", getHpidFromSession(session));
            } else {
                log.warn("Sender session {} is not connected", getHpidFromSession(session));
            }
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
            String receiverUserId = chatMessageDto.getReceiverUserId();
            WebSocketSession receiverSession = sessions.get(receiverUserId);
            if (receiverSession != null && receiverSession.isOpen()) {
                chatService.handleMessage(chatMessageDto);
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
                log.info("Message sent from {} to {}", chatMessageDto.getSenderUserId(), receiverUserId);
            } else {
                log.warn("Receiver {} is not connected", receiverUserId);
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
            String hpid = getHpidFromSession(session);
            sessions.remove(hpid);
            hospitalConnectionStatusService.updateConnectionStatus(hpid, false);

            hospitalService.deleteHospitalMatchesByUserHpid(hpid);
            log.info("User disconnected: " + hpid);
        } catch (Exception e) {
            log.error("Error during connection closure", e);
        }
    }

    public void sendMessage(String senderHpid, String receiverHpid, Map<String, Object> message) {
        try {
            WebSocketSession senderSession = sessions.get(senderHpid);
            if (senderSession != null && senderSession.isOpen()) {
                Optional<User> senderOptional = userRepository.findByHpid(senderHpid);
                if (senderOptional.isPresent()) {
                    User senderUser = senderOptional.get();
                    String senderName = senderUser.getUsername();
                    String senderPhoneNumber = senderUser.getPhone_number();
                    message.put("senderName", senderName);
                    message.put("senderPhoneNumber", senderPhoneNumber);
                } else {
                    log.warn("Sender {} not found in userRepository", senderHpid);
                }

                WebSocketSession receiverSession = sessions.get(receiverHpid);
                if (receiverSession != null && receiverSession.isOpen()) {
                    receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                    log.info("Message sent from {} to {}", senderHpid, receiverHpid);
                } else {
                    log.warn("Receiver {} is not connected", receiverHpid);
                }
            } else {
                log.warn("Sender {} is not connected", senderHpid);
            }
        } catch (IOException e) {
            log.error("Error sending message from {} to {}", senderHpid, receiverHpid, e);
        }
    }

    private String getHpidFromSession(WebSocketSession session) {
        return session.getUri().getQuery().split("=")[1];
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("error", errorMessage))));
        } catch (IOException e) {
            log.error("Error sending error message", e);
        }
    }
}