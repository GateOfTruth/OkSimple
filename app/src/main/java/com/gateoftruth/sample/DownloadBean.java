package com.gateoftruth.sample;

public class DownloadBean {
    private int progress;
    private String url;
    private String fileName;

    public DownloadBean(int progress, String url, String fileName) {
        this.progress = progress;
        this.url = url;
        this.fileName = fileName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
