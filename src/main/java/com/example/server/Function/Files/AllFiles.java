package com.example.server.Function.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class AllFiles {
    private String getPadding(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= level; i++) {
            sb.append("|   ");
        }
        return sb.toString();
    }

    private void listChild(File file, int level, BufferedWriter writer) throws IOException {
        if (file.isDirectory()) {
            writer.write(getPadding(level) + "|__" + file.getName() + "\n");
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child != null) {
                        this.listChild(child, level + 1, writer);
                    }
                }
            }
        } else {
            writer.write(getPadding(level) + "|__" + file.getName() + "\n");
        }
    }

    public void saveToFile(File dirFile, File outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            listChild(dirFile, 0, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAllFiles(String FOLDER_PATH, String filePath) {
        AllFiles allFiles = new AllFiles();
        File dirFile = new File(FOLDER_PATH);
        File outputFile = new File(filePath + File.separator + "output.txt");
        allFiles.saveToFile(dirFile, outputFile);
        System.out.println("File đã được lưu tại: " + outputFile.getAbsolutePath());
    }
}
