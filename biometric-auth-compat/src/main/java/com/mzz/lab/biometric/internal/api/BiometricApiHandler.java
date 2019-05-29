package com.mzz.lab.biometric.internal.api;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.BiometricManagerV23;
import com.mzz.lab.biometric.internal.BiometricCallbackV28;

public class BiometricApiHandler extends AbstractApiHandler {
    @Override
    protected void init(BiometricCallback biometricCallback) {

    }


    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(final BiometricCallback biometricCallback) {
        this.cancellationDelegate = new CancellationDelegateLegacy();

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
                .authenticate((CancellationSignal) cancellationDelegate.get(), context.getMainExecutor(),
                        new BiometricCallbackV28(biometricCallback));
    }
}
