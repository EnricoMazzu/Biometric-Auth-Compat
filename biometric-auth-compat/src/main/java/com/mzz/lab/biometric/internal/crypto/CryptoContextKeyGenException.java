package com.mzz.lab.biometric.internal.crypto;

import com.mzz.lab.biometric.models.errors.CryptoContextInitException;

import java.security.GeneralSecurityException;

public class CryptoContextKeyGenException extends CryptoContextInitException {
    public static final int CRYPTO_KEY_INIT_ERROR = 10;
    public CryptoContextKeyGenException(GeneralSecurityException exc) {
        super(CRYPTO_KEY_INIT_ERROR,exc);
    }
}
