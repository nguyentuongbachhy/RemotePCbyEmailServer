package com.example.server.User;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    public UserInformation findByUsername(String username);
}
