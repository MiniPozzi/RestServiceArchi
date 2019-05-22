package com.rest.objects;

public class FileShared {

    private String filename;

    private String accessToken;

    public FileShared() {

    }

    public FileShared(String filename) {
        this.filename = filename;
        this.accessToken = Utils.createToken();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
