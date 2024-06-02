package gong.server_api.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private String affiliation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
