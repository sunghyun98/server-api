package gong.server_api.domain.dto;

import gong.server_api.domain.entity.user.Chat;
import gong.server_api.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingRoomDto {

    private Long id;

    private LocalDateTime createdAt;

    private String roomName;

    private String senderId;

    private String content;

    private String receiverId;

}
