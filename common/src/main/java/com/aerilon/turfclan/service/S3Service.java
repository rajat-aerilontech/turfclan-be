package com.aerilon.turfclan.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String uploadFile(MultipartFile file, String fileName, String folderName, boolean overwrite) throws IOException;

    byte[] downloadFile(String key);

    List<String> listFiles();

    String preSignedUrl(String key, int expiryMinutes);

    void deleteFile(String key);
}
