package com.instar.lab1.DTO;


public class Music {
    private String filePath;
    private String fileName;

    public Music(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }
}
