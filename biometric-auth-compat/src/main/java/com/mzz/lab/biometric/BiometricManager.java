package com.mzz.lab.biometric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.internal.api.biometricV28.BiometricApiHandler;
import com.mzz.lab.biometric.internal.api.fingerprint.FingerprintCompatApiHandler;
import com.mzz.lab.biometric.models.AuthenticationPurpose;
import com.mzz.lab.biometric.models.CryptoParams;

/**
 * This class manage the authentication flow.
 */
public class BiometricManager {

    private final BiometricBuilder biometricBuilder;
    private AbstractApiHandler apiHandler;

    protected BiometricManager(final BiometricBuilder biometricBuilder) {
        this.biometricBuilder = biometricBuilder;
    }


    /**
     * Start the authentication flow.
     * @param context the android context (N.B: this should be a valid ui context
     * @param biometricCallback the callback used as a listener
     */
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

    /**
     * Cancel the pending authentication
     */
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

    /**
     * Builder class
     */
    public static class BiometricBuilder {

        private String title;
        private String subtitle;
        private String description;
        private String negativeButtonText;
        private AuthenticationPurpose authenticationPurpose;
        private CryptoParams cryptoParams;

        private BiometricBuilder() {

        }

        /**
         * Set the title of the sheet dialog
         * @param title the title
         * @return the builder
         */
        public BiometricBuilder setTitle(@NonNull final String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the subtitle of the sheet dialog
         * @param subtitle the subtitle
         * @return the builder
         */
        public BiometricBuilder setSubtitle(@NonNull final String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * Set a description message
         * @param description the description
         * @return the builder
         */
        public BiometricBuilder setDescription(@NonNull final String description) {
            this.description = description;
            return this;
        }


        /**
         * Set the negative button text
         * @param negativeButtonText the button text
         * @return the builder
         */
        public BiometricBuilder setNegativeButtonText(@NonNull final String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * Set the authentication purpose. This parameter depending on your needs
         * @param authenticationPurpose
         * @return the builder
         */
        public BiometricBuilder setAuthenticationPurpose(@NonNull AuthenticationPurpose authenticationPurpose) {
            this.authenticationPurpose = authenticationPurpose;
            return this;
        }

        /**
         * Set the crypto params to use inside the crypto layer
         * @param cryptoParams the crypto params
         * @return the builder
         */
        public BiometricBuilder setCryptoParams(CryptoParams cryptoParams) {
            this.cryptoParams = cryptoParams;
            return this;
        }

        /**
         * Build a new {@link BiometricManager} instance
         * @return the new instance
         * @throws IllegalArgumentException if invalid arguments will be detected
         */
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
