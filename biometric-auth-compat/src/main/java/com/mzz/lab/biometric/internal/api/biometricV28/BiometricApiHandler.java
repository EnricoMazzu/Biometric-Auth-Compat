package com.mzz.lab.biometric.internal.api.biometricV28;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.BiometricCallbackV28;
import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;

public class BiometricApiHandler extends AbstractApiHandler {

    @Override
    protected void init(Context context, BiometricCallback biometricCallback) {
        displayBiometricPrompt(context,biometricCallback);
    }


    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(Context context, final BiometricCallback biometricCallback) {
        this.cancellationDelegate = new CancellationDelegateLegacy();
        CryptoContext cryptoContext = getCryptoContext();
        if(cryptoContext == null){
            //TODO send specific error;
            biometricCallback.onBiometricAuthenticationInternalError("invalid crypto context");
            return;
        }
        BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cryptoContext.getCipher());
        new BiometricPrompt.Builder(context)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButton(negativeButtonText, context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        biometricCallback.onAuthenticationCancelled();
                    }
                })
                .build()
                .authenticate(cryptoObject,(CancellationSignal) cancellationDelegate.get(), context.getMainExecutor(),
                        new BiometricCallbackV28(biometricCallback));
    }
}
