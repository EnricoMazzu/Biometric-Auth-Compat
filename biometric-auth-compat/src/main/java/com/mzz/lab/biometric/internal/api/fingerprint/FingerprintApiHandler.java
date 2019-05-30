package com.mzz.lab.biometric.internal.api.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.internal.ui.BiometricDialogV23;
import com.mzz.lab.biometric.R;
import com.mzz.lab.biometric.internal.BiometricResultFactory;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.models.BiometricAuthenticationResult;

import java.lang.ref.WeakReference;

public class FingerprintApiHandler extends AbstractApiHandler {

    private BiometricDialogV23 biometricDialogV23;

    public FingerprintApiHandler() {
    }

    @Override
    protected void init(Context context, BiometricCallback biometricCallback) {
        setupWithLegacy(context,biometricCallback);

    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void setupWithLegacy(Context context, final BiometricCallback biometricCallback) {
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

        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cryptoContext.getCipher()), (android.os.CancellationSignal) cancellationDelegate.get(), 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                handleOnAuthenticationError(errorCode, errString, biometricCallback);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                handleOnAuthenticationHelp(helpCode, helpString, biometricCallback);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                handleOnAuthenticationSucceeded(BiometricResultFactory.from(result), biometricCallback);
            }

            @Override
            public void onAuthenticationFailed() {
                Context context = contextWeakReference.get();
                handleOnAuthenticationFailed(context, biometricCallback);
            }
        },null);

        displayBiometricDialog(context);
    }

    protected void handleOnAuthenticationError(int errorCode, CharSequence errString, BiometricCallback biometricCallback) {
        updateStatus(String.valueOf(errString));
        if(errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED){
            biometricCallback.onAuthenticationCancelled();
        }else{
            biometricCallback.onAuthenticationError(errorCode, errString);
        }
    }

    protected void handleOnAuthenticationHelp(int helpCode, CharSequence helpString, BiometricCallback biometricCallback) {
        updateStatus(String.valueOf(helpString));
        biometricCallback.onAuthenticationHelp(helpCode, helpString);
    }


    protected void handleOnAuthenticationSucceeded(BiometricAuthenticationResult result, BiometricCallback biometricCallback) {
        dismissDialog();
        biometricCallback.onAuthenticationSuccessful(result);
    }

    protected void handleOnAuthenticationFailed(Context context, BiometricCallback biometricCallback) {
        if(context != null){
            updateStatus(context.getString(R.string.biometric_failed));
        }
        biometricCallback.onAuthenticationFailed();
    }



    protected void displayBiometricDialog(Context context) {
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
