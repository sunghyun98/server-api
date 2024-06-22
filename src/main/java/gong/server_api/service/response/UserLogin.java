package gong.server_api.service.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {
    private String email;
    private String password;

}
