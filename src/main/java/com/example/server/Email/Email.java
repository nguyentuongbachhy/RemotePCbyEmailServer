package com.example.server.Email;

public class Email {
    private String senderEmail;
    private String senderPassword;
    private String recipientEmail;
    private String subject;
    private String content;
    private String filePath;
    private String imagePath;

    public Email() {
    }

    public Email(String senderEmail, String senderPassword, String recipientEmail, String subject, String content,
            String filePath, String imagePath) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.content = content;
        this.filePath = filePath;
        this.imagePath = imagePath;
    }
    public String getSenderEmail() {
        return senderEmail;
    }
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    public String getSenderPassword() {
        return senderPassword;
    }
    public void setSenderPassword(String senderPassword) {
        this.senderPassword = senderPassword;
    }
    public String getRecipientEmail() {
        return recipientEmail;
    }
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    
}
