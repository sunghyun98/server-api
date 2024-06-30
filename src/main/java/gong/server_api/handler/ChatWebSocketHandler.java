package gong.server_api.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import gong.server_api.service.request.PythonRequest;
import gong.server_api.service.response.PythonResponse;
import gong.server_api.service.PythonService;
import gong.server_api.domain.dto.chat.ChatMessage;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import gong.server_api.domain.dto.searchList.HospitalCombined;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 사용자의 hpid를 키로 사용하여 WebSocketSession을 관리하는 Map
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
            // 클라이언트가 연결되면 실행되는 로직
            String hpid = getHpidFromSession(session);
            sessions.put(hpid, session);

            // 이미 연결된 병원 리스트 가져오기
            List<String> connectedHospitals = hospitalConnectionStatusService.getConnectedHospitals();

            // 현재 연결된 모든 병원의 HPID 목록을 JSON 형식으로 변환
            String connectedHospitalsJson = new ObjectMapper().writeValueAsString(connectedHospitals);

            // 로그 출력
           /* for (String connectedHospital : connectedHospitals) {
                // HPID로 사용자 조회하여 organizationName 가져오기
                userRepository.findByHpid(connectedHospital).ifPresent(user -> {
                    String organizationName = user.getOrganization_name();
                    // 새로 연결된 클라이언트에게 현재 연결된 병원 목록 전송
                    try {
                        session.sendMessage(new TextMessage(organizationName));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("another connected: hpid={}, organizationName={}", connectedHospital, organizationName);
                });
            }
*/
            hospitalConnectionStatusService.updateConnectionStatus(hpid, true);
            session.sendMessage(new TextMessage("안녕하세요!\n증상을 입력해 주시면 가장 가까운 응급실을 찾아드리겠습니다."));
            log.info("User connected: " + hpid);

        } catch (Exception e) {
            log.error("Error during connection establishment", e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            // 클라이언트로부터 메시지를 수신할 때 호출됨
            String payload = message.getPayload();
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
            log.info("Received message: {}", chatMessage);

            // 현재 시간으로 createdAt 필드 설정
            chatMessage.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            // 메시지 처리 로직
            //chatService.handleMessage(chatMessage);

            // PythonRequest 객체 생성 및 데이터 설정
            PythonRequest pythonRequest = new PythonRequest();
            pythonRequest.setSituation(chatMessage.getContent());
            pythonRequest.setLatitude(Double.parseDouble(chatMessage.getLatitude()));
            pythonRequest.setLongitude(Double.parseDouble(chatMessage.getLongitude()));

            PythonResponse pythonResponse = pythonService.executePythonScript(pythonRequest);
            log.info("pythonResponse={}",pythonResponse);

            // latitude와 longitude 변수에 값 설정
            double latitude = pythonRequest.getLatitude();
            double longitude = pythonRequest.getLongitude();
            log.info("latitude={}",latitude);

            List<HospitalCombined> hospitalAddress = hospitalService.getHospitalAddress(latitude, longitude, pythonResponse);
            // 안전 메시지 가져오기
            String safetyMessage = pythonResponse.getSafetyMessage();
            log.info("safetyMessage={}", safetyMessage);

            // 병원 목록과 안전 메시지를 포함한 JSON 객체 생성
            Map<String, Object> response = new HashMap<>();
            response.put("safetyMessage", safetyMessage);
            response.put("hospital", hospitalAddress);
            response.put("hospitalCount", hospitalAddress.size());
            log.info("hospitalAddress={}",hospitalAddress);
            // 수신자에게 병원 목록 전달

            // 전송자의 세션 가져오기
            WebSocketSession senderSession = sessions.get(getHpidFromSession(session));
            if (senderSession != null && senderSession.isOpen()) {
                senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
                log.info("Hospital list sent to {}", getHpidFromSession(session));
            } else {
                log.warn("Sender session {} is not connected", getHpidFromSession(session));
            }

            /*
            // 수신자에게 메시지 전달
            String receiverUserId = chatMessage.getReceiverUserId();
            WebSocketSession receiverSession = sessions.get(receiverUserId);
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                log.info("Message sent from {} to {}", chatMessage.getSenderUserId(), receiverUserId);
            } else {
                log.warn("Receiver {} is not connected", receiverUserId);
            }
            */

        } catch (IOException e) {
            log.error("Error processing message", e);
            sendErrorMessage(session, "Invalid message format");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendErrorMessage(session, "An unexpected error occurred");
        }

        }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            // 클라이언트와 연결이 끊길 때 호출됨
            String hpid = getHpidFromSession(session);
            sessions.remove(hpid);
            hospitalConnectionStatusService.updateConnectionStatus(hpid, false);
            log.info("User disconnected: " + hpid);
        } catch (Exception e) {
            log.error("Error during connection closure", e);
        }
    }
    public void sendMessage(String senderHpid, String receiverHpid, Map<String, Object> message) {
        try {
            // 송신자 세션 가져오기
            WebSocketSession senderSession = sessions.get(senderHpid);
            if (senderSession != null && senderSession.isOpen()) {
                // 송신자 정보에서 이름과 전화번호 가져오기
                Optional<User> senderOptional = userRepository.findByHpid(senderHpid);
                if (((Optional<?>) senderOptional).isPresent()) {
                    User senderUser = senderOptional.get();
                    String senderName = senderUser.getUsername();
                    String senderPhoneNumber = senderUser.getPhone_number();

                    // 메시지에 보내는 사람 정보 추가
                    message.put("senderName", senderName);
                    message.put("senderPhoneNumber", senderPhoneNumber);
                } else {
                    log.warn("Sender {} not found in userRepository", senderHpid);
                }

                // 수신자 세션 가져오기
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
        // 예시로, 세션의 URI 또는 헤더에서 hpid를 추출
        // 실제 구현은 프로젝트 요구사항에 맞게 수정
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