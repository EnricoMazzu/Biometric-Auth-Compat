package com.mzz.lab.biometric.internal.api.biometricV28;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.BiometricCallbackV28;
import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.models.errors.CryptoContextInitException;

public class BiometricApiHandler extends AbstractApiHandler {

    @Override
    protected void startAuthentication(Context context, BiometricCallback biometricCallback) throws CryptoContextInitException {
        displayBiometricPrompt(context,biometricCallback);
    }


    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(Context context, final BiometricCallback biometricCallback) throws CryptoContextInitException {
        this.cancellationDelegate = new CancellationDelegateLegacy();
        CryptoContext cryptoContext = getCryptoContext();
        BiometricPrompt.CryptoObject cryptoObject = toCryptoObject(cryptoContext);
        BiometricPrompt prompt = new BiometricPrompt.Builder(context)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButton(negativeButtonText, context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        biometricCallback.onAuthenticationCancelled();
                    }
                })
                .build();

        if(cryptoObject != null){
            prompt.authenticate(cryptoObject,(CancellationSignal) cancellationDelegate.get(), context.getMainExecutor(),
                    new BiometricCallbackV28(biometricCallback));
        }else{
            prompt.authenticate((CancellationSignal) cancellationDelegate.get(), context.getMainExecutor(),
                    new BiometricCallbackV28(biometricCallback));
        }

    }

    @TargetApi(Build.VERSION_CODES.P)
    @Nullable
    private BiometricPrompt.CryptoObject toCryptoObject(CryptoContext cryptoContext) {
        if(cryptoContext == null){
            return null;
        }
        return new BiometricPrompt.CryptoObject(cryptoContext.getCipher());
    }
}
