package com.example.server.Function.Lists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayloadRequest {
    private int pidCode;
    private String nameApp;
    private String senderEmail;
    private String senderPassword;
}
