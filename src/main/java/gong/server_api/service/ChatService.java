    package gong.server_api.service;

    import gong.server_api.domain.dto.chat.ChatMessage;
    import gong.server_api.domain.dto.chat.HospitalChatMessage;
    import gong.server_api.domain.entity.user.Chat;
    import gong.server_api.domain.entity.user.ChattingRoom;
    import gong.server_api.domain.entity.user.HospitalAi;
    import gong.server_api.domain.entity.user.User;
    import gong.server_api.handler.ChatWebSocketHandler;
    import gong.server_api.repository.ChattingRoomRepository;
    import gong.server_api.repository.HospitalAiRepo;
    import gong.server_api.repository.PersonalChatRepository;
    import gong.server_api.repository.UserRepository;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.bind.annotation.*;

    import java.sql.Timestamp;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;
    import java.util.stream.Collectors;


    @Slf4j
    @Service
    public class ChatService {

        private final PersonalChatRepository personalChatRepository;
        private final UserRepository userRepository;
        private final ChattingRoomRepository chattingRoomRepository;

        private final HospitalAiRepo hospitalAiRepo;

        @Autowired
        public ChatService(PersonalChatRepository personalChatRepository, UserRepository userRepository, ChattingRoomRepository chattingRoomRepository, HospitalAiRepo hospitalAiRepo) {
            this.personalChatRepository = personalChatRepository;
            this.userRepository = userRepository;
            this.chattingRoomRepository = chattingRoomRepository;
            this.hospitalAiRepo = hospitalAiRepo;
        }

        @Transactional
        public void handleMessage(ChatMessage chatMessage) {
            // Sender와 Receiver 사용자 찾기
            User senderUser = userRepository.findByHpid(chatMessage.getSenderUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sender user ID"));
            log.info("senderUser={}", senderUser.getHpid());

            User receiverUser = userRepository.findByHpid(chatMessage.getReceiverUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid receiver user ID"));
            log.info("receiverUser={}", receiverUser.getHpid());

            // 채팅방 찾기 또는 생성
            ChattingRoom chattingRoom = findOrCreateRoom(chatMessage);
            log.info("chattingRoom={}", chattingRoom.getId());

            // Chat 엔티티 생성 및 설정
            Chat chat = new Chat();
            chat.setSenderUser(senderUser);
            chat.setReceiverUser(receiverUser);
            chat.setContent(chatMessage.getContent());
            chat.setChattingRoom(chattingRoom);
            chat.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // 메시지 저장
            personalChatRepository.save(chat);
            log.info("Message from {} to {} in room {} saved.", chatMessage.getSenderUserId(), chatMessage.getReceiverUserId(), chatMessage.getChattingRoomId());
        }

        //채팅방 찾기
        private ChattingRoom findOrCreateRoom(ChatMessage chatMessage) {
            Long chattingRoomId = chatMessage.getChattingRoomId();

            if (chattingRoomId != null) {
                return chattingRoomRepository.findById(chattingRoomId)
                        .orElseGet(() -> createRoom(chatMessage));
            } else {
                return createRoom(chatMessage);
            }
        }

        //채팅방 생성
        private ChattingRoom createRoom(ChatMessage chatMessage) {
            // 보내는 사용자 정보 가져오기
            User sender = userRepository.findByHpid(chatMessage.getSenderUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found for HPID: " + chatMessage.getSenderUserId()));

            // 받는 사용자 정보 가져오기
            User receiver = userRepository.findByHpid(chatMessage.getReceiverUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Receiver not found for HPID: " + chatMessage.getReceiverUserId()));

            // 채팅방 생성
            ChattingRoom room = new ChattingRoom();
            room.setCreatedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            room.setSenderId(sender);
            room.setReceiverId(receiver);

            // 채팅방 저장
            return chattingRoomRepository.save(room);
        }


        public List<ChatMessage> findReceivedChatList(String receiverHpid) {
            log.info("Finding received chats for receiverHpid = {}", receiverHpid);

            User receiverUser = userRepository.findByHpid(receiverHpid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for HPID: " + receiverHpid));
            log.info("Receiver found: {}", receiverUser);

            List<Chat> chats = personalChatRepository.findByReceiverUser(receiverUser);

            List<ChatMessage> chatDTOs = chats.stream()
                    .map(chat -> {
                        ChatMessage chatDTO = new ChatMessage();
                        chatDTO.setSenderUserId(chat.getSenderUser().getUsername());
                        chatDTO.setReceiverUserId(chat.getReceiverUser().getUsername());
                        chatDTO.setContent(chat.getContent());
                        chatDTO.setCreatedAt(chat.getCreatedAt());
                        chatDTO.setReadAt(chat.getReadAt());
                        chatDTO.setChattingRoomId(chat.getChattingRoom().getId());
                        return chatDTO;
                    })
                    .collect(Collectors.toList());

            return chatDTOs;
        }

        public List<HospitalChatMessage> findHospitalChat(String hpid) {
            log.info("Finding received chats for receiverHpid = {}", hpid);

            User receiverUser = userRepository.findByHpid(hpid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for HPID: " + hpid));
            log.info("Receiver found: {}", receiverUser);

            List<Chat> chats = personalChatRepository.findByReceiverUser(receiverUser);

            List<HospitalChatMessage> hospitalChatMessages = chats.stream().map(chat -> {
                // Find the hospital name (dutyName) based on sender's hpid
                String hospital = chat.getReceiverUser().getHpid();
                Optional<HospitalAi> optionalHospitalAi = hospitalAiRepo.findByHpid(hospital);

                String dutyName = optionalHospitalAi.map(HospitalAi::getDutyName).orElse("Unknown Hospital");

                // Create HospitalChatMessage
                return new HospitalChatMessage(
                        chat.getSenderUser().getOrganization_name(),
                        dutyName,
                        chat.getContent(),
                        chat.getCreatedAt()
                );
            }).collect(Collectors.toList());

            return hospitalChatMessages;
        }

    }