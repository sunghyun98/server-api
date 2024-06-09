    package gong.server_api.domain.dto;

    import gong.server_api.domain.entity.user.Role;
    import lombok.*;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserJoinDto {

        private String username;
        private String email;
        private String password;
        private String phoneNumber;
        private Role role;
        private String affiliation;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
