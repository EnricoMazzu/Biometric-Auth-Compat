package com.mzz.lab.biometricsample;

public class EncryptedData {
    private String base64Data;
    private String base64Iv;

    public EncryptedData(String base64Data, String base64Iv) {
        this.base64Data = base64Data;
        this.base64Iv = base64Iv;
    }

    public String getBase64Iv() {
        return base64Iv;
    }

    public String getBase64Data() {
        return base64Data;
    }
}
