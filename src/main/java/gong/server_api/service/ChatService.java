    package gong.server_api.service;

    import gong.server_api.domain.dto.ChatMessageDto;
    import gong.server_api.domain.dto.ChattingRoomDto;
    import gong.server_api.domain.dto.HospitalChatMessageDto;
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
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.sql.Timestamp;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.time.ZonedDateTime;
    import java.util.List;
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
        public ChatMessageDto handleMessage(ChatMessageDto chatMessageDto) {
            // Sender와 Receiver 사용자 찾기
            User senderUser = userRepository.findByHpid(chatMessageDto.getSenderUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sender user ID"));
            log.info("senderUser={}", senderUser.getHpid());

            User receiverUser = userRepository.findByHpid(chatMessageDto.getReceiverUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid receiver user ID"));
            log.info("receiverUser={}", receiverUser.getHpid());

            // 채팅방 찾기 또는 생성
            ChattingRoom chattingRoom = findOrCreateRoom(chatMessageDto);
            log.info("chattingRoom={}", chattingRoom.getId());

            chatMessageDto.setCreatedAt(Timestamp.from(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()));
            chatMessageDto.setChattingRoomId(chattingRoom.getId());
            chatMessageDto.setOrganization(senderUser.getOrganization_name());

            // Chat 엔티티 생성 및 설정
            Chat chat = new Chat();
            chat.setSenderUser(senderUser);
            chat.setReceiverUser(receiverUser);
            chat.setContent(chatMessageDto.getContent());
            chat.setChattingRoom(chattingRoom);
            chat.setCreatedAt(chatMessageDto.getCreatedAt());

            // 메시지 저장
            personalChatRepository.save(chat);
            log.info("Message from {} to {} in room {} saved.", chatMessageDto.getSenderUserId(), chatMessageDto.getReceiverUserId(), chatMessageDto.getChattingRoomId());
            return chatMessageDto;
        }

        //채팅방 찾기
        private ChattingRoom findOrCreateRoom(ChatMessageDto chatMessageDto) {
            Long chattingRoomId = chatMessageDto.getChattingRoomId();

            if (chattingRoomId != null) {
                return chattingRoomRepository.findById(chattingRoomId)
                        .orElseGet(() -> createRoom(chatMessageDto));
            } else {
                return createRoom(chatMessageDto);
            }
        }

        //채팅방 생성
        public ChattingRoom createRoom(ChatMessageDto chatMessageDto) {
            // 보내는 사용자 정보 가져오기
            User sender = userRepository.findByEmail(chatMessageDto.getSenderUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found for USER: " + chatMessageDto.getSenderUserId()));

            // 받는 사용자 정보 가져오기
            User receiver = userRepository.findByEmail(chatMessageDto.getReceiverUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Receiver not found for USER: " + chatMessageDto.getReceiverUserId()));

            // 채팅방 생성
            ChattingRoom room = new ChattingRoom();
            room.setCreatedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            room.setSenderId(sender);
            room.setReceiverId(receiver);

            // 채팅방 저장
            return chattingRoomRepository.save(room);
        }
        public ChattingRoomDto createHospitalRoom(ChattingRoomDto chattingRoomDto) {
            // 받는 사용자 정보 가져오기
            User receiver = userRepository.findByHpid(chattingRoomDto.getReceiverId())
                    .orElseThrow(() -> new IllegalArgumentException("Receiver not found for USER: " + chattingRoomDto.getReceiverId()));

            // 보낸 사용자 정보 가져오기
            User sender = userRepository.findByEmail(chattingRoomDto.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found for USER: " + chattingRoomDto.getSenderId()));

            chattingRoomDto.setRoomName(receiver.getOrganization_name());

            // 채팅방 생성
            ChattingRoom room = new ChattingRoom();
            room.setCreatedAt(LocalDateTime.now());
            room.setRoomName(receiver.getOrganization_name());
            room.setSenderId(sender);
            room.setReceiverId(receiver);

            // 채팅방 저장
            ChattingRoom savedRoom = chattingRoomRepository.save(room);

            // 생성된 채팅방 ID를 DTO에 설정
            chattingRoomDto.setId(savedRoom.getId());

            // DTO 반환
            return chattingRoomDto;
        }
        public List<ChatMessageDto> findReceivedChatList(String receiverHpid) {
            log.info("Finding received chats for receiverHpid = {}", receiverHpid);

            User receiverUser = userRepository.findByHpid(receiverHpid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for HPID: " + receiverHpid));
            log.info("Receiver found: {}", receiverUser);

            List<Chat> chats = personalChatRepository.findByReceiverUser(receiverUser);

            List<ChatMessageDto> chatDTOs = chats.stream()
                    .map(chat -> {
                        ChatMessageDto chatDTO = new ChatMessageDto();
                        chatDTO.setSenderUserId(chat.getSenderUser().getUsername());
                        chatDTO.setReceiverUserId(chat.getReceiverUser().getUsername());
                        chatDTO.setContent(chat.getContent());
                        chatDTO.setCreatedAt(chat.getCreatedAt());
                        chatDTO.setChattingRoomId(chat.getChattingRoom().getId());
                        return chatDTO;
                    })
                    .collect(Collectors.toList());

            return chatDTOs;
        }

        public List<HospitalChatMessageDto> findHospitalChat(String hpid) {
            log.info("Finding received chats for receiverHpid = {}", hpid);

            User receiverUser = userRepository.findByHpid(hpid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for HPID: " + hpid));
            log.info("Receiver found: {}", receiverUser);

            List<Chat> chats = personalChatRepository.findByReceiverUser(receiverUser);

            List<HospitalChatMessageDto> hospitalChatMessageDtos = chats.stream().map(chat -> {
                // Find the hospital name (dutyName) based on sender's hpid
                String hospital = chat.getReceiverUser().getHpid();
                Optional<HospitalAi> optionalHospitalAi = hospitalAiRepo.findByHpid(hospital);

                String dutyName = optionalHospitalAi.map(HospitalAi::getDutyName).orElse("Unknown Hospital");

                // Create HospitalChatMessage
                return new HospitalChatMessageDto(
                        chat.getSenderUser().getOrganization_name(),
                        dutyName,
                        chat.getContent(),
                        chat.getCreatedAt()
                );
            }).collect(Collectors.toList());

            return hospitalChatMessageDtos;
        }

    }