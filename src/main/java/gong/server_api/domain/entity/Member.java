package gong.server_api.domain.entity;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private Long id;
    private String username;
    private String email;
    private String password;
}
