package com.mzz.lab.biometric.internal.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.BiometricDialogV23;
import com.mzz.lab.biometric.R;
import com.mzz.lab.biometric.internal.BiometricResultFactory;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.internal.crypto.CryptoContextInitException;

import java.util.UUID;

public class FingerprintApiHandler extends AbstractApiHandler {

    private BiometricDialogV23 biometricDialogV23;

    public FingerprintApiHandler() {
    }

    @Override
    protected void init(BiometricCallback biometricCallback) {
        setupWithLegacy(biometricCallback);

    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void setupWithLegacy(final BiometricCallback biometricCallback) {
        cancellationDelegate = new CancellationDelegateLegacy();
        //FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
        CryptoContext cryptoContext = getCryptoContext();
        if(cryptoContext == null){
            //TODO send specific error;
            biometricCallback.onBiometricAuthenticationInternalError("invalid crypto context");
            return;
        }
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if(fingerprintManager == null){
            biometricCallback.onBiometricAuthenticationInternalError("FingerprintManager is null");
            return;
        }

        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cryptoContext.getCipher()), (android.os.CancellationSignal) cancellationDelegate.get(), 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                updateStatus(String.valueOf(errString));
                if(errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED){
                    biometricCallback.onAuthenticationCancelled();
                }else{
                    biometricCallback.onAuthenticationError(errorCode, errString);
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                updateStatus(String.valueOf(helpString));
                biometricCallback.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                dismissDialog();
                biometricCallback.onAuthenticationSuccessful(BiometricResultFactory.from(result));
            }

            @Override
            public void onAuthenticationFailed() {
                updateStatus(context.getString(R.string.biometric_failed));
                biometricCallback.onAuthenticationFailed();
            }
        },null);

        displayBiometricDialog();
    }


    protected void displayBiometricDialog() {
        biometricDialogV23 = new BiometricDialogV23(context,cancellationDelegate);
        biometricDialogV23.setTitle(title);
        //biometricDialogV23.setSubtitle(subtitle);
        biometricDialogV23.setDescription(description);
        biometricDialogV23.setButtonText(negativeButtonText);
        biometricDialogV23.show();
    }

    protected void dismissDialog() {
        if(biometricDialogV23 != null) {
            biometricDialogV23.dismiss();
        }
    }

    protected void updateStatus(String status) {
        if(biometricDialogV23 != null) {
            biometricDialogV23.updateStatus(status);
        }
    }

}
