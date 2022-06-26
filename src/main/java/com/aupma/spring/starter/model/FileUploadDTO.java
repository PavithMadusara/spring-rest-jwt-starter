package com.aupma.spring.starter.model;

import lombok.Data;

@Data
public class FileUploadDTO {
    private String fileName;
    private String downloadURL;
    private String fileType;
    private long fileSize;

    public FileUploadDTO(String fileName, String downloadURL, String contentType, long fileSize) {
        this.fileName = fileName;
        this.downloadURL = downloadURL;
        this.fileType = contentType;
        this.fileSize = fileSize;
    }
}
