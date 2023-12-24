package com.example.server.Email;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.Announce.Announce;
import com.example.server.Function.Files.AllFiles;
import com.example.server.Function.KeyLog.KeyLogRequest;
import com.example.server.Function.Lists.PayloadRequest;
import com.example.server.Function.Lists.ProcessPC;
import com.example.server.Security.EndPointsConfig;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class EmailController {
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private ProcessPC processPC;

    @Autowired
    private AllFiles allFiles;

    private BufferedWriter writer;

    private String FilePath = "D:/btl/server/assets/store";

    private String FolderPath = "D:\\btl\\server";

    private String subject = "Response from Server";

    @PostMapping("/send-message")
    public ResponseEntity<?> sendEmailMessage(@RequestBody Email email) {
        if (emailService == null) {
            return ResponseEntity.badRequest().body(new Announce("EmailService is not initialized"));
        }

        emailService.sendMessage(email.getSenderEmail(), email.getSenderPassword(), email.getRecipientEmail(), email.getSubject(), email.getContent(), email.getFilePath(), email.getImagePath());
        
        String task = emailService.getRequest();
        System.out.println(task);
        switch (task) {
            case "Get List Processes":
                return handleProcessListRequest(email);
            case "ScreenShot":
                return handleScreenshotRequest(email);
            
            case "Keylog":
                return startLogging();

            case "Shutdown":
                return handleShutdownProcessRequest(email);
            
            case "Get File":
                return handleGetFileFromFilePath(email);

            case "Get All Files":
                return handleSendAllFiles(email);

            default:
                emailService.responseMessage(email.getSenderEmail(), "Thank you for your response!",
                "<html><body>"
                + "<h2>Thank you for Your Feedback!</h2>"
                +"<p>We appreciate your valuable feedback. Your insights are crucial to us, and they inspire us to continuously improve.</p>"
                +"<p>We are committed to addressing your concerns and enhancing our services to provide the best experience for all our users.</p>"
                +"<p>Thank you once again, and have a greate day!</p>"
                +"<p>Best regards,</p>"
                +"<p>Nguyễn Tường Bách Hỷ</p>"
                +"<p>Kí túc xá khu B, đại học quốc gia thành phố Hồ Chí Minh</p>"
                +"<p>0911076983</p>"
                + "</body></html>", null, null);
                return ResponseEntity.ok().body(new Announce("Respond successfully"));
        }
    }

    @PostMapping("/log-key")
    public void logKey(@RequestBody KeyLogRequest keyLogRequest) {
        String key = keyLogRequest.getKey();
        try {
            if(writer != null) {
                writer.write("Key Pressed: " + key);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/stop-logging")
    public ResponseEntity<?> stopLogging(@RequestBody Email email) {
        try {
            if(writer != null) {
                writer.close();
            }
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createSuccessResponse(email.getSenderEmail(), "Response from request: Catch the key <br/>Status: Success", null), FilePath + File.separator + "keylog.txt", null);
            return ResponseEntity.ok().body(new Announce("Stop Successfully"));
        } catch (Exception e) {
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createErrorResponse(email.getSenderEmail(), "Response from request: Catch the key <br/>Status: Fail"), null, null);
            return ResponseEntity.badRequest().body(new Announce("Error stopping key logging"));
        }
    }

    @PostMapping("/kill-app")
    public ResponseEntity<?> kill(@RequestBody PayloadRequest payload) {
        String subjectSender = "Request from client: Request Kill App";
        String contentSender = "Request: Kill App";
        emailService.sendMessage(payload.getSenderEmail(), payload.getSenderPassword(), EndPointsConfig.senderMailServer, subjectSender, contentSender, null, null);
        
        String task = emailService.getRequest();
        System.out.println(task);

        if(task.equals("Kill App")) {
            boolean check = processPC.kill(payload.getPidCode());
            if(check) {
                emailService.responseMessage(payload.getSenderEmail(), subject, emailService.createSuccessResponse(payload.getSenderEmail(), "Response request: Kill App has PID: " + payload.getPidCode() + "<br/>" + "Status: Success", null), null, null);
                return ResponseEntity.ok().body(new Announce("Kill App Successly"));
            } else {
                emailService.responseMessage(payload.getSenderEmail(), subject, emailService.createErrorResponse(payload.getSenderEmail(), "Response request: Kill App has PID: " + payload.getPidCode() + "<br/>" + "Status: Failed"), null, null);
                return ResponseEntity.badRequest().body(new Announce("Error when killing application"));
            }
        }
        emailService.responseMessage(payload.getSenderEmail(), subject, emailService.createErrorResponse(payload.getSenderEmail(), "Response request: Kill App has PID: " + payload.getPidCode() + "<br/>" + "Status: Failed"), null, null);
        return ResponseEntity.badRequest().body(new Announce("Error when sending email"));
    }
    
    @PostMapping("/start-app")
    public ResponseEntity<?> start(@RequestBody PayloadRequest payload) {
        String subjectSender = "Request from client: Request Start App";
        String contentSender = "Request: Start App";
        emailService.sendMessage(payload.getSenderEmail(), payload.getSenderPassword(), EndPointsConfig.senderMailServer, subjectSender, contentSender, null, null);
        
        String task = emailService.getRequest();
        System.out.println(task);
        System.out.println(payload.getNameApp());
        if(task.equals("Start App")) {
            boolean check = processPC.start(payload.getNameApp());
            if(check) {
                emailService.responseMessage(payload.getSenderEmail(), subject, emailService.createSuccessResponse(payload.getSenderEmail(), "Response request: Start App " + payload.getNameApp() + "<br/>" + "Status: Success", null), null, null);
                return ResponseEntity.ok().body(new Announce("Start Successfully"));
            }
            else {
                emailService.responseMessage(payload.getSenderEmail(), subject, emailService.createErrorResponse(payload.getSenderEmail(), "Response request: Start App " + payload.getNameApp() + "<br/>" + "Status: Failed"), null, null);
                return ResponseEntity.badRequest().body(new Announce("Error when starting application"));
            }
        }
        emailService.responseMessage(payload.getSenderEmail(), subject, emailService.createErrorResponse(payload.getSenderEmail(), "Response request: Start App " + payload.getNameApp() + "<br/>" + "Status: Failed"), null, null);
        return ResponseEntity.badRequest().body(new Announce("Error when sending email"));
    }
    
    private ResponseEntity<?> handleProcessListRequest(Email email) {
        boolean check = ProcessPC.listProcesses(FilePath);
        if(check) {
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createSuccessResponse(email.getSenderEmail(), "Response request : Get list processes <br/>Status: Success", null), FilePath + "/process.txt", null);
            return ResponseEntity.ok().body(new Announce("Get process list Successly"));
        } else {
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createErrorResponse(email.getSenderEmail(), "Response request : Get list processes <br/>Status: Failed"), null, null);
            return ResponseEntity.badRequest().body(new Announce("Failed to get process list"));
        }
    }

    private ResponseEntity<?> handleScreenshotRequest(Email email) {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenImg = robot.createScreenCapture(screenRect);

            File outputFile = new File(FilePath + File.separator + "screenshot.png");
            ImageIO.write(screenImg, "png", outputFile);

            System.out.println("Screenshot successfully and saved into " + outputFile.getAbsolutePath());
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createSuccessResponse(email.getSenderEmail(), "Response request : Screenshot <br/>Status: Success", null), null, FilePath + "/screenshot.png");
            return ResponseEntity.ok().body("Screenshot successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Screenshot failed!");
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createErrorResponse(email.getSenderEmail(), "Response request : Screenshot <br/>Status: Failed"), null, null);
            return ResponseEntity.badRequest().body(new Announce("Screenshot failed!"));
        }
    }

    private ResponseEntity<?> handleShutdownProcessRequest(Email email) {
        emailService.responseMessage(email.getSenderEmail(), subject, emailService.createSuccessResponse(email.getSenderEmail(), "Response request: Shutdown <br/>Status: Success", null), null, null);
        processPC.shutdownProcess();
        return ResponseEntity.ok().body(new Announce("Shutdown Success"));
    }

    private ResponseEntity<?> handleSendAllFiles(Email email) {
        allFiles.saveAllFiles(FolderPath, FilePath);
        emailService.responseMessage(email.getSenderEmail(), subject, emailService.createSuccessResponse(email.getSenderEmail(), "Response request: Get all files in server<br/>Status: Success", null), FilePath + File.separator + "output.txt", null);
        return ResponseEntity.ok().body(new Announce("Get all files successfully"));
    }

    private ResponseEntity<?> handleGetFileFromFilePath(Email email) {
        File file = new File("D:/btl/" + email.getFilePath());
        if(file.exists()) {
            emailService.responseMessage(email.getSenderEmail(), subject, emailService.createSuccessResponse(email.getSenderEmail(), "Response request: Get File has filepath: " + email.getFilePath() + "<br/>Status: Success", null), email.getFilePath(), null);
            return ResponseEntity.ok().body(new Announce("Get File successfully"));
        }
        return ResponseEntity.badRequest().body(new Announce("Your filepath is not exist!"));
    }

    private void clearLogFile(String filePath) {
        try {
            BufferedWriter clearWriter = new BufferedWriter(new FileWriter(filePath));
            clearWriter.write("");
            clearWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResponseEntity<?> startLogging() {
        try {
            String logFilePath = FilePath + File.separator + "keylog.txt";
            clearLogFile(logFilePath);
            writer = new BufferedWriter(new FileWriter(logFilePath, true));
            return ResponseEntity.ok().body(new Announce("Key logging started!"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new Announce("Error starting key logging!"));
        }
    }
}
