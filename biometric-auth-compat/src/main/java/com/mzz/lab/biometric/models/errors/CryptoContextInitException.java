package com.mzz.lab.biometric.models.errors;

/**
 * Represent a failure during the internal crypto init flow
 */
public class CryptoContextInitException extends Exception {

    public static final int GENERIC_KEYSTORE_EXCEPTION = 0;
    public static final int INIT_CIPHER_EXCEPTION = 10;
    public static final int INVALIDATED_KEY_EXCEPTION = 20;

    private int errorCode;

    public CryptoContextInitException(Exception cause) {
        this(GENERIC_KEYSTORE_EXCEPTION,cause);
    }

    /**
     * Constructor
     * @param errorCode the error code
     * @param cause the underlying exception
     */
    public CryptoContextInitException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor
     * @param errorCode the error code
     * @param message the exception message
     */
    public CryptoContextInitException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Return the error code the explain better the source of the error
     * @return
     */
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
