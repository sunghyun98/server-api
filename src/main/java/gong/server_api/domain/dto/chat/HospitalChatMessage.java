package gong.server_api.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/*
병원 채팅 메시지
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalChatMessage {
    private String senderUserId;
    private String dutyName;
    private String content;
    private Timestamp createdAt;
}