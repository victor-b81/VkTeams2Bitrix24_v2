package com.mydomain.vkteams2bitrix24.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
@Service
public class EncodeFileToBase64 {
    public static String encodeFileToBase64(String filePath) {
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            log.info("Encode file to Base64 done (" + file.getName() + ")");
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            log.info("Encode file to Base64 errore (" + file.getName() + ")");
            return null;
        }
    }

}
