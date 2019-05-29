package com.mzz.lab.biometric.internal;

import android.annotation.TargetApi;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.mzz.lab.biometric.models.BiometricAuthenticationResult;
import com.mzz.lab.biometric.models.CryptoEntity;

public class BiometricResultFactory {
    @TargetApi(Build.VERSION_CODES.M)
    public static BiometricAuthenticationResult from(FingerprintManager.AuthenticationResult authenticationResult){
        FingerprintManager.CryptoObject crypto = authenticationResult.getCryptoObject();
        CryptoEntity cryptoEntity =
                crypto == null ?  null : CryptoEntity.newBuilder()
                        .setCipher(crypto.getCipher())
                        .setMac(crypto.getMac())
                        .setSignature(crypto.getSignature())
                        .build();

        return new BiometricAuthenticationResult(cryptoEntity);

    }

    public static BiometricAuthenticationResult from(FingerprintManagerCompat.AuthenticationResult authenticationResult){
        FingerprintManagerCompat.CryptoObject crypto = authenticationResult.getCryptoObject();
        CryptoEntity cryptoEntity =
                crypto == null ?  null : CryptoEntity.newBuilder()
                        .setCipher(crypto.getCipher())
                        .setMac(crypto.getMac())
                        .setSignature(crypto.getSignature())
                        .build();

        return new BiometricAuthenticationResult(cryptoEntity);

    }

    @TargetApi(Build.VERSION_CODES.P)
    public static BiometricAuthenticationResult from(BiometricPrompt.AuthenticationResult authenticationResult){
        BiometricPrompt.CryptoObject crypto = authenticationResult.getCryptoObject();
        CryptoEntity cryptoEntity =
                crypto == null ?  null : CryptoEntity.newBuilder()
                        .setCipher(crypto.getCipher())
                        .setMac(crypto.getMac())
                        .setSignature(crypto.getSignature())
                        .build();

        return new BiometricAuthenticationResult(cryptoEntity);
    }
}
