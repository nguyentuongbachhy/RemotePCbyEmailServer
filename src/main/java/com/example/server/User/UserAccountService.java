package com.example.server.User;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.server.Announce.Announce;
import com.example.server.Email.EmailService;
import com.example.server.Security.EndPointsConfig;

@Service
public class UserAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private EmailService emailService;


    private String createActiveCode() {
        return UUID.randomUUID().toString();
    }


    private void sendEmail(String email, String activationCode, String purpose) {
        String subject;
        String text;
    
        if ("registration".equals(purpose)) {
            subject = "Please activate your account at RemotePC Web";
            String url = "http://localhost:3000/activate/"+ email + "/" + activationCode;
            text = "Dear " + email + ",<br/><br/>"
                + "Thank you for registering at RemotePC Web. "
                + "To activate your account, please click link: <br/><br/>"
                + "<a href="+ url +">"+ url +"</a>"
                + "<br/><br/>"
                + "Enter this code in the activation section on our website.<br/><br/>"
                + "If you did not register on RemotePC Web, please ignore this email.<br/><br/>"
                + "Best regards,<br/>"
                + "The RemotePC Web Team";
        } else if ("passwordReset".equals(purpose)) {
            subject = "Password Reset Request at RemotePC Web";
            String url = "http://localhost:3000/reset-password/"+ email + "/" + activationCode;
            text = "Dear " + email + ",<br/><br/>"
                + "You have requested to reset your password at RemotePC Web. "
                + "To reset your password, please click link: <br/><br/>"
                + "<a href="+ url +">"+ url +"</a>"
                + "<br/><br/>"
                + "If you did not request a password reset, please ignore this email.<br/><br/>"
                + "Best regards,<br/>"
                + "The RemotePC Web Team";
        } else {
            return;
        }
    
        ResponseEntity<?> response = emailService.sendMessage(EndPointsConfig.senderMailServer, EndPointsConfig.senderPasswordServer, email, subject, text, null, null);
        System.out.println(response.getStatusCode());
    }
    
    public ResponseEntity<?> UserRegistration(UserInformation userInformation) {
        if(userRepository.existsByEmail(userInformation.getEmail())) {
            return ResponseEntity.badRequest().body(new Announce("Email has already exist!!!"));
        }
        if(userRepository.existsByUsername(userInformation.getUsername())) {
            return ResponseEntity.badRequest().body(new Announce("Phone number has already exist!!!"));
        }
        
        String endCryptPassword = passwordEncoder.encode(userInformation.getPassword());
        userInformation.setPassword(endCryptPassword);

        userInformation.setActivationCode(createActiveCode());
        userInformation.setActivated(false);

        UserInformation userRegister = userRepository.save(userInformation);
        
        sendEmail(userInformation.getEmail(), userInformation.getActivationCode(), "registration");

        return ResponseEntity.ok().body(new Announce("Registration success! \n Hello " + userRegister.getLastName()));
    }

    public ResponseEntity<?> ActivateAccount(String email, String activationCode) {
        
        UserInformation userInformation = userRepository.findByEmail(email);

        if(userInformation == null) {
            return ResponseEntity.badRequest().body(new Announce("Email is not exist"));
        }

        if(userInformation.isActivated()) {
            return ResponseEntity.badRequest().body(new Announce("Email is activated!"));
        }

        if(activationCode.equals(userInformation.getActivationCode())) {
            userInformation.setActivated(true);
            userRepository.save(userInformation);
            return ResponseEntity.ok("Activate email success");
        }

        else {
            return ResponseEntity.badRequest().body(new Announce("Activation Code is incorrect"));
        }
    }

    public ResponseEntity<?> updateAccount(String username, String email, String password) {
        UserInformation updateUser = userRepository.findByUsername(username);
        if (updateUser == null) {
            return ResponseEntity.badRequest().body(new Announce("Account does not exist!"));
        } else {
            if (!updateUser.getEmail().equals(email)) {
                return ResponseEntity.badRequest().body(new Announce("Email is incorrect!"));
            } else {
                String endCryptPassword = passwordEncoder.encode(password);
                updateUser.setPassword(endCryptPassword);
                
                updateUser.setActivationCode(createActiveCode());
                updateUser.setActivated(false);
    
                UserInformation userInformation = userRepository.save(updateUser);
    
                sendEmail(updateUser.getEmail(), updateUser.getActivationCode(), "passwordReset");
    
                return ResponseEntity.ok().body(new Announce("Your account was updated!\n Hello " + userInformation.getUsername()));
            }
        }
    }
    
}
