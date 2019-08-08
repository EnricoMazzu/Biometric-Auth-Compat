package com.mzz.lab.biometric.internal;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.mzz.lab.biometric.AuthenticationCallback;


@RequiresApi(api = Build.VERSION_CODES.P)
public class BiometricCallbackV28 extends BiometricPrompt.AuthenticationCallback {

    private AuthenticationCallback authenticationCallback;
    public BiometricCallbackV28(AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
    }


    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        authenticationCallback.onAuthenticationSuccessful(BiometricResultFactory.from(result));
    }


    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        authenticationCallback.onAuthenticationHelp(helpCode, helpString);
    }


    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        authenticationCallback.onAuthenticationError(errorCode, errString);
    }


    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        authenticationCallback.onAuthenticationFailed();
    }
}
