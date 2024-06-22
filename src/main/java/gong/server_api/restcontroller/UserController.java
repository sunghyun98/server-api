package gong.server_api.restcontroller;


import gong.server_api.domain.dto.UserJoinDto;
import gong.server_api.service.UserService;
import gong.server_api.service.response.UserLogin;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //회원가입 컨트롤러
    // 회원가입 컨트롤러
    @PostMapping("/api/v1/join")
    public ResponseEntity<String> join(@RequestBody UserJoinDto userJoinDto){
        log.info("userJoinDto ={}", userJoinDto);
        userService.userJoin(userJoinDto);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

}