package com.example.server.Function.Lists;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class ProcessPC {
    Runtime runtime;

    public ProcessPC() {
        try {
            this.runtime = Runtime.getRuntime();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean listProcesses(String filePath) {
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + File.separator + "process.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            reader.close();

            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean kill(int pid) {
        String command;
        String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
            command = "taskkill /F /PID " + pid;
        } else {
            command = "kill -9" + pid;
        }

        try {
            ProcessBuilder processBuilder;
            if(os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if(exitCode != 0) {
                System.out.println("Error: Failed to kill process with PID " + pid);
                return false;
            } else {
                System.out.println("Process with PID " +  pid + " was killed");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean start(String name) {
        String command;
        String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
            command = "start " + name;
        } else if(os.contains("nix")) {
            command = name;
        } else if (os.contains("mac")) {
            command = "open " + name;
        } else {
            System.out.println("Error: Unsupported operating system.");
            return false;
        }

        try {
            ProcessBuilder processBuilder;
            if(os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if(exitCode != 0) {
                System.out.println("Error: Failed to start application with name " + name);
                return false;
            } else {
                System.out.println("Server started application with name " + name);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void shutdownProcess() {
        String command;
        String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")) {
            command = "shutdown -s -t 0";
        } else if(os.contains("nux") || os.contains("mac")) {
            command = "shutdown -h now";
        } else {
            System.out.println("Error: Unsupported operating system.");
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Shut down successfully");
            } else {
                System.out.println("Shut down failed. Exit Code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}