package com.mzz.lab.biometric.internal.api;

import android.content.Context;
import android.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.CancellationDelegate;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.internal.crypto.CryptoContextInitException;
import com.mzz.lab.biometric.models.AuthenticationPurpose;
import com.mzz.lab.biometric.models.CryptoParams;

public abstract class AbstractApiHandler {

    //private static final String KEY_NAME = UUID.randomUUID().toString();

    protected String title;
    protected String subtitle;
    protected String description;
    protected String negativeButtonText;
    protected AuthenticationPurpose authenticationPurpose = AuthenticationPurpose.NONE;
    protected CancellationDelegate cancellationDelegate;
    protected CryptoParams cryptoParams;

    public AbstractApiHandler(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public AuthenticationPurpose getAuthenticationPurpose() {
        return authenticationPurpose;
    }

    public void setAuthenticationPurpose(AuthenticationPurpose authenticationPurpose) {
        this.authenticationPurpose = authenticationPurpose;
    }

    public CryptoParams getCryptoParams() {
        return cryptoParams;
    }

    public void setCryptoParams(CryptoParams cryptoParams) {
        this.cryptoParams = cryptoParams;
    }

    public void cancelAuthentication(){
        if(!cancellationDelegate.isCanceled()){
            cancellationDelegate.cancel();
        }
    }


    protected abstract void startAuthentication(Context context, BiometricCallback biometricCallback) throws CryptoContextInitException;


    public void authenticate(Context context,BiometricCallback biometricCallback){
        try {
            startAuthentication(context,biometricCallback);
        } catch (CryptoContextInitException e) {
            biometricCallback.onBiometricAuthenticationInternalError(e);
        }
    }


    protected CryptoContext getCryptoContext() throws CryptoContextInitException {
        if(authenticationPurpose == null || authenticationPurpose == AuthenticationPurpose.NONE){
            return null;
        }
        return new CryptoContext(cryptoParams);

    }


    protected static class CancellationDelegateLegacy extends CancellationDelegate<CancellationSignal> {
        public CancellationDelegateLegacy() {
            super(new android.os.CancellationSignal());
        }

        @Override
        public void cancel() {
            cancellationSignal.cancel();
        }

        @Override
        public boolean isCanceled() {
            return cancellationSignal.isCanceled();
        }
    }


}
