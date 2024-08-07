    package gong.server_api.domain.dto;

    import gong.server_api.domain.entity.user.Role;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Pattern;
    import jakarta.validation.constraints.Size;
    import lombok.*;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserJoinDto {

        @NotBlank(message = "유저이름을 입력하세요.")
        private String username;

        @Email(message = "Email should be valid")
        @NotBlank(message = "이메일을 입력하세요.")
        private String email;

        @NotBlank(message = "패스워드를 입력하세요.")
        @Size(min = 4, message = "비밀번호는 네자리 이상 입력해야합니다.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[!@#$%^*&()+=]).+$", message = "비밀번호는 특수문자를 포함해야합니다.")
        private String password;

        private String phoneNumber;
        private Role role;
        private String organizationName;
        private String hpid;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }