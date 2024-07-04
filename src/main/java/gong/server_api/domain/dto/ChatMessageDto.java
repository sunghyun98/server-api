package gong.server_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto extends BaseMessageDto{
    private String organization;
    private String senderUserId;
    private String receiverUserId;
    private String content;
    private Long chattingRoomId;
    private Timestamp createdAt;
}