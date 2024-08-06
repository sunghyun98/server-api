package gong.server_api.service;

import gong.server_api.CustomUserDetails;
import gong.server_api.domain.entity.user.User;
import gong.server_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // DB에서 userEmail로 사용자 조회
        User userData = userRepository.findByEmail(email).orElseThrow();

        if (userData != null) {
            return new CustomUserDetails(userData);
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}