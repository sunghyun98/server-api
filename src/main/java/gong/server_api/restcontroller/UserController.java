package gong.server_api.restcontroller;


import gong.server_api.CustomUserDetails;
import gong.server_api.domain.dto.HospitalConnectionStatusDto;
import gong.server_api.domain.dto.UserDto;
import gong.server_api.domain.dto.UserJoinDto;
import gong.server_api.domain.entity.user.Role;
import gong.server_api.repository.UserRepository;
import gong.server_api.service.HospitalConnectionStatusService;
import gong.server_api.service.UserService;
import gong.server_api.service.response.ConnectionStatusResponseDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping("/connection")
    public List<ConnectionStatusResponseDto> ConnectionStatus(){
        CustomUserDetails userDetails = (CustomUserDetails ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Role role = userDetails.getRole();
        return hospitalConnectionStatusService.getConnectedStatus(String.valueOf(role));
    }



}