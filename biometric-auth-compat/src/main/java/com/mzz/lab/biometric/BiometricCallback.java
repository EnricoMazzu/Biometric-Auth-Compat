package com.mzz.lab.biometric;

import com.mzz.lab.biometric.models.errors.CryptoContextInitException;
import com.mzz.lab.biometric.models.BiometricAuthenticationResult;

public interface BiometricCallback {

    void onSdkVersionNotSupported();

    void onBiometricAuthenticationNotSupported();

    void onBiometricAuthenticationNotAvailable();

    void onBiometricAuthenticationPermissionNotGranted();

    void onBiometricAuthenticationInternalError(CryptoContextInitException error);

    void onAuthenticationFailed();

    void onAuthenticationCancelled();

    void onAuthenticationSuccessful(BiometricAuthenticationResult authenticationResult);

    void onAuthenticationHelp(int helpCode, CharSequence helpString);

    void onAuthenticationError(int errorCode, CharSequence errString);
}
