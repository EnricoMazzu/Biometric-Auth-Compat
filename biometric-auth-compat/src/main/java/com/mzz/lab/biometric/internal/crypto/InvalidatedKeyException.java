package com.mzz.lab.biometric.internal.crypto;

import com.mzz.lab.biometric.models.errors.CryptoContextInitException;

public class InvalidatedKeyException extends CryptoContextInitException {

    public InvalidatedKeyException() {
        super(INVALIDATED_KEY_EXCEPTION,"Required key was permanently invalidated");
    }
}
