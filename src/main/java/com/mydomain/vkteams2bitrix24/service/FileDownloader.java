package com.mydomain.vkteams2bitrix24.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileDownloader {
    public static void downloadFile(String fileUrl, String destinationPath) {
        if (!Files.exists(Paths.get("temp"))){
            try {
                Files.createDirectory(Paths.get("temp"));
                log.info("Create directory: temp");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            log.info("File downloaded successfully (" + Paths.get(new URL(fileUrl).getPath()).getFileName().toString() + ")");
        } catch (FileNotFoundException e) {
            log.info("Errore. File not found: " + fileUrl);
            log.info(e.getMessage());
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            log.info("Errore. MalformedURLException: " + fileUrl);
            log.info(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("Errore. IOException: " + fileUrl);
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
