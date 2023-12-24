package com.example.server.Email;

import org.springframework.http.ResponseEntity;

public interface EmailService {
    public ResponseEntity<?> sendMessage(String senderMail, String senderPassword, String to, String subject, String text, String filePath, String imagePath);

    public void responseMessage(String to, String subject, String text, String filePath, String imagePath);

    public String getRequest();

    public String createSuccessResponse(String senderEmail, String message, String additionalInfo);

    public String createErrorResponse(String senderEmail, String errorMessage);
}
