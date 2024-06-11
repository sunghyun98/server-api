package gong.server_api.domain.dto.chat;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    long id;
    String senderUserId;
    String receiverUserId;
    String groupId;
    String content;
    Timestamp read_at;
    Timestamp created_at;


}
