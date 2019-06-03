package com.mzz.lab.biometric.models.errors;

public class CryptoContextInitException extends Exception {

    public static final int GENERIC_KEYSTORE_EXCEPTION = 0;
    public static final int INIT_CIPHER_EXCEPTION = 10;
    public static final int INVALIDATED_KEY_EXCEPTION = 20;

    private int errorCode;

    public CryptoContextInitException(Exception cause) {
        this(GENERIC_KEYSTORE_EXCEPTION,cause);
    }

    public CryptoContextInitException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public CryptoContextInitException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "CryptoContextInitException{" +
                "errorCode=" + errorCode +
                "} " + super.toString();
    }
}
