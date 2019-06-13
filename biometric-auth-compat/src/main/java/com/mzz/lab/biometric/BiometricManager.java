package com.mzz.lab.biometric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.internal.api.biometricV28.BiometricApiHandler;
import com.mzz.lab.biometric.internal.api.fingerprint.FingerprintCompatApiHandler;
import com.mzz.lab.biometric.models.AuthenticationPurpose;
import com.mzz.lab.biometric.models.CryptoParams;

public class BiometricManager {

    private final BiometricBuilder biometricBuilder;
    private AbstractApiHandler apiHandler;

    protected BiometricManager(final BiometricBuilder biometricBuilder) {
        this.biometricBuilder = biometricBuilder;
    }


    public void authenticate(Context context,@NonNull final BiometricCallback biometricCallback) {
        if(!BiometricUtils.isSdkVersionSupported()) {
            biometricCallback.onSdkVersionNotSupported();
            return;
        }

        if(!BiometricUtils.isPermissionGranted(context)) {
            biometricCallback.onBiometricAuthenticationPermissionNotGranted();
            return;
        }

        if(!BiometricUtils.isHardwareSupported(context)) {
            biometricCallback.onBiometricAuthenticationNotSupported();
            return;
        }

        if(!BiometricUtils.hasEnrolledFingerprints(context)) {
            biometricCallback.onBiometricAuthenticationNotAvailable();
            return;
        }

        //displayBiometricDialog(biometricCallback);
        authenticateWithApiHandler(context,biometricCallback);

    }

    public void cancelAuthentication(){
        if(apiHandler != null){
            apiHandler.cancelAuthentication();
        }
    }



    private void authenticateWithApiHandler(Context context,BiometricCallback biometricCallback) {
        this.apiHandler = createAndSetupApiHandler();
        this.apiHandler.authenticate(context,biometricCallback);
    }

    private AbstractApiHandler createAndSetupApiHandler() {
        AbstractApiHandler abstractApiHandler;

        if(BiometricUtils.isBiometricPromptEnabled()){
            abstractApiHandler = new BiometricApiHandler();
        }else{
            abstractApiHandler = new FingerprintCompatApiHandler();
        }
        setupApiHandler(abstractApiHandler,this.biometricBuilder);
        return abstractApiHandler;
    }

    private void setupApiHandler(AbstractApiHandler abstractApiHandler, BiometricBuilder biometricBuilder) {
        abstractApiHandler.setTitle(biometricBuilder.title);
        abstractApiHandler.setSubtitle(biometricBuilder.subtitle);
        abstractApiHandler.setDescription(biometricBuilder.description);
        abstractApiHandler.setNegativeButtonText(biometricBuilder.negativeButtonText);
        abstractApiHandler.setAuthenticationPurpose(biometricBuilder.authenticationPurpose);
        abstractApiHandler.setCryptoParams(biometricBuilder.cryptoParams);
    }


    public static BiometricBuilder newBuilder(){
        return new BiometricBuilder();
    }

    public static class BiometricBuilder {

        private String title;
        private String subtitle;
        private String description;
        private String negativeButtonText;
        private AuthenticationPurpose authenticationPurpose;
        private CryptoParams cryptoParams;

        private BiometricBuilder() {

        }

        public BiometricBuilder setTitle(@NonNull final String title) {
            this.title = title;
            return this;
        }

        public BiometricBuilder setSubtitle(@NonNull final String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public BiometricBuilder setDescription(@NonNull final String description) {
            this.description = description;
            return this;
        }


        public BiometricBuilder setNegativeButtonText(@NonNull final String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public BiometricBuilder setAuthenticationPurpose(@NonNull AuthenticationPurpose authenticationPurpose) {
            this.authenticationPurpose = authenticationPurpose;
            return this;
        }

        public BiometricBuilder setCryptoParams(CryptoParams cryptoParams) {
            this.cryptoParams = cryptoParams;
            return this;
        }

        public BiometricManager build() throws IllegalArgumentException {
            checkParameters();
            return new BiometricManager(this);
        }

        private void checkParameters() throws IllegalArgumentException{
            if(title == null){
                throw new IllegalArgumentException("Biometric Dialog title cannot be null");
            }
            if(subtitle == null){
                throw new IllegalArgumentException("Biometric Dialog subtitle cannot be null");
            }
            if(description == null){
                throw new IllegalArgumentException("Biometric Dialog description cannot be null");
            }
            if(negativeButtonText == null){
                throw new IllegalArgumentException("Biometric Dialog negative button text cannot be null");
            }
            if(authenticationPurpose != AuthenticationPurpose.NONE && cryptoParams == null){
                throw new IllegalArgumentException("Biometric Dialog crypto params cannot be null when authentication purpose is ENC_DEC_DATA");
            }
        }
    }
}
