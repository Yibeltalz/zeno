package com.zeno.security;

import com.zeno.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // JWT subject is the user's UUID
        try {
            UUID userId = UUID.fromString(username);
            return userRepository.findById(userId)
                    .map(user -> User.withUsername(user.getId().toString())
                            .password(user.getPassword())
                            .roles("USER")
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        } catch (IllegalArgumentException e) {
            // fallback: try by email
            return userRepository.findByEmail(username)
                    .map(user -> User.withUsername(user.getId().toString())
                            .password(user.getPassword())
                            .roles("USER")
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        }
    }
}
