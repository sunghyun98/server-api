package gong.server_api.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String senderUserId;
    private String receiverUserId;
    private String content;
    private Long chattingRoomId;
    private Timestamp readAt;
    private Timestamp createdAt;

    //AI전송 메시지
    private String latitude;
    private String longitude;
}