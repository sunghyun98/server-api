package gong.server_api.service;

import gong.server_api.domain.dto.UserJoinDto;
import gong.server_api.domain.entity.user.Role;
import gong.server_api.domain.entity.user.User;
import gong.server_api.repository.MembersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final MembersRepository membersRepository;

   // private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(MembersRepository membersRepository/*BCryptPasswordEncoder bCryptPasswordEncoder*/) {
        this.membersRepository = membersRepository;
    }

    public void userJoin(UserJoinDto userJoinDto) {
        String username = userJoinDto.getUsername();
        String email = userJoinDto.getEmail();
        String password = userJoinDto.getPassword();
        Role role = userJoinDto.getRole();
        String affiliation = userJoinDto.getAffiliation();
        String phoneNumber = userJoinDto.getPhoneNumber();
        LocalDateTime createdAt = userJoinDto.getCreatedAt();
        LocalDateTime updatedAt = userJoinDto.getUpdatedAt();

        boolean isExist = membersRepository.existsByEmail(email);

        if (isExist) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setAffiliation(affiliation);
        user.setPhone_number(phoneNumber);
        user.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
        user.setUpdatedAt(updatedAt != null ? updatedAt : LocalDateTime.now());

        membersRepository.save(user);
    }
}
