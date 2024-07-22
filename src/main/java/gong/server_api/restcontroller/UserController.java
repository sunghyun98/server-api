package gong.server_api.restcontroller;


import gong.server_api.domain.dto.UserDto;
import gong.server_api.domain.dto.UserJoinDto;
import gong.server_api.repository.UserRepository;
import gong.server_api.service.HospitalConnectionStatusService;
import gong.server_api.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final HospitalConnectionStatusService hospitalConnectionStatusService;
    public UserController(UserService userService, UserRepository userRepository, HospitalConnectionStatusService hospitalConnectionStatusService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.hospitalConnectionStatusService = hospitalConnectionStatusService;
    }

    //회원가입 컨트롤러
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody @Valid UserJoinDto userJoinDto) {
        try {
            userService.userJoin(userJoinDto);
            return ResponseEntity.ok("User successfully registered");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public UserDto findUserInfo() {
        return userService.findUserImfo();
    }

    //역할이 소방관인 유저는 병원 목록만 뜨게 한다.
    //역할이 병원인 유저는 소방관 목록만 뜨게 한다.
    //jwt에서 role을 추출해서 role에 따라서 접속상태 확인
    //자신을 제외한 접속자 나오게


}