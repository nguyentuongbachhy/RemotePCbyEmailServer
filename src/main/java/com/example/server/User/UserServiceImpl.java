package com.example.server.User;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.server.Role.Role;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                    .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInformation userLoaded = findByUsername(username);

        if(userLoaded == null) {
            throw new UsernameNotFoundException("Account is not exist!");
        }
        User user = new User(userLoaded.getUsername(), userLoaded.getPassword(), rolesToAuthorities(userLoaded.getRoles()));
        return user;
    }

    @Override
    public UserInformation findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
