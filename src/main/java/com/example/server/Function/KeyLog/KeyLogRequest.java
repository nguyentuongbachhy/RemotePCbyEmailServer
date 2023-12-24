package com.example.server.Function.KeyLog;

public class KeyLogRequest {
    private String key;

    public KeyLogRequest() {
    }
    
    public KeyLogRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
