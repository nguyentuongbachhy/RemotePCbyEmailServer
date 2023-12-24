package com.example.server.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.Announce.Announce;
import com.example.server.Config.Jwt.JwtResponse;
import com.example.server.Config.Jwt.JwtService;
import com.example.server.Security.EndPointsConfig;

@RestController
@CrossOrigin(origins = EndPointsConfig.front_end_host)
@RequestMapping("/api/user-account")
public class UserController {
    
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Validated @RequestBody UserInformation userInformation) {
        try {
            ResponseEntity<?> response = userAccountService.UserRegistration(userInformation);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Announce("Internal Server Error"));
        }
    }

    @GetMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestParam String email, @RequestParam String activationCode) {
        try {
            ResponseEntity<?> response = userAccountService.ActivateAccount(email, activationCode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Announce("Internal Server Error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Validated @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserInformation user = userRepository.findByUsername(loginRequest.getUsername());

            if(authentication != null && authentication.isAuthenticated() && user.isActivated()) {
                final String jwt = jwtService.generateToken(loginRequest.getUsername());
                return ResponseEntity.ok(new JwtResponse(jwt));
            }
            
        } catch (AuthenticationException error) {
            return ResponseEntity.badRequest().body(new Announce("Username or Password is incorrect!"));
        }

        return ResponseEntity.badRequest().body(new Announce("Authentication failed!"));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> updatePassword(@Validated @RequestBody LoginRequest loginRequest) {
        try {
            ResponseEntity<?> response = userAccountService.updateAccount(loginRequest.getUsername(), loginRequest.getEmail(), loginRequest.getPassword());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Announce("Internal Server Error"));
        }
    }
}
