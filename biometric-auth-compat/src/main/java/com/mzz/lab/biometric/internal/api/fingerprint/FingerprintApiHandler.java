package com.mzz.lab.biometric.internal.api.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.Nullable;

import com.mzz.lab.biometric.AuthenticationCallback;
import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.models.errors.CryptoContextInitException;
import com.mzz.lab.biometric.internal.ui.BiometricView;
import com.mzz.lab.biometric.R;
import com.mzz.lab.biometric.internal.BiometricResultFactory;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.models.BiometricAuthenticationResult;

import java.lang.ref.WeakReference;

public class FingerprintApiHandler extends AbstractApiHandler {

    private BiometricView biometricView;

    public FingerprintApiHandler() {
    }

    @Override
    protected void startAuthentication(Context context, AuthenticationCallback authenticationCallback) throws CryptoContextInitException {
        setupWithLegacy(context, authenticationCallback);

    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void setupWithLegacy(Context context, final AuthenticationCallback authenticationCallback) throws CryptoContextInitException {
        cancellationDelegate = new CancellationDelegateLegacy();
        CryptoContext cryptoContext = getCryptoContext();
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if(fingerprintManager == null){
            //TODO review this
            authenticationCallback.onBiometricAuthenticationInternalError(null);
            return;
        }

        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        FingerprintManager.CryptoObject cryptoObject = toCryptoObject(cryptoContext);
        fingerprintManager.authenticate(cryptoObject, (android.os.CancellationSignal) cancellationDelegate.get(), 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                handleOnAuthenticationError(errorCode, errString, authenticationCallback);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                handleOnAuthenticationHelp(helpCode, helpString, authenticationCallback);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                handleOnAuthenticationSucceeded(BiometricResultFactory.from(result), authenticationCallback);
            }

            @Override
            public void onAuthenticationFailed() {
                Context context = contextWeakReference.get();
                handleOnAuthenticationFailed(context, authenticationCallback);
            }
        },null);

        displayBiometricDialog(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    private FingerprintManager.CryptoObject toCryptoObject(CryptoContext cryptoContext) {
        if(cryptoContext == null){
            return null;
        }
        return new FingerprintManager.CryptoObject(cryptoContext.getCipher());
    }

    protected void handleOnAuthenticationError(int errorCode, CharSequence errString, AuthenticationCallback authenticationCallback) {
        updateStatus(String.valueOf(errString));
        if(errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED){
            authenticationCallback.onAuthenticationCancelled();
        }else{
            authenticationCallback.onAuthenticationError(errorCode, errString);
        }
    }

    protected void handleOnAuthenticationHelp(int helpCode, CharSequence helpString, AuthenticationCallback authenticationCallback) {
        updateStatus(String.valueOf(helpString));
        authenticationCallback.onAuthenticationHelp(helpCode, helpString);
    }


    protected void handleOnAuthenticationSucceeded(BiometricAuthenticationResult result, AuthenticationCallback authenticationCallback) {
        dismissDialog();
        authenticationCallback.onAuthenticationSuccessful(result);
    }

    protected void handleOnAuthenticationFailed(Context context, AuthenticationCallback authenticationCallback) {
        if(context != null){
            updateStatus(context.getString(R.string.biometric_failed));
        }
        authenticationCallback.onAuthenticationFailed();
    }



    protected void displayBiometricDialog(Context context) {
        biometricView = new BiometricView(context,cancellationDelegate);
        biometricView.setTitle(title);
        //biometricView.setSubtitle(subtitle);
        biometricView.setDescription(description);
        biometricView.setButtonText(negativeButtonText);
        biometricView.show();
    }

    protected void dismissDialog() {
        if(biometricView != null) {
            biometricView.dismiss();
        }
    }

    protected void updateStatus(String status) {
        if(biometricView != null) {
            biometricView.updateStatus(status);
        }
    }

}
