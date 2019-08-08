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
public class BiometricAuthenticator {

    private final Builder biometricBuilder;
    private AbstractApiHandler apiHandler;

    protected BiometricAuthenticator(final Builder biometricBuilder) {
        this.biometricBuilder = biometricBuilder;
    }


    /**
     * Start the authentication flow.
     * @param context the android context (N.B: this should be a valid ui context
     * @param authenticationCallback the callback used as a listener
     */
    public void authenticate(Context context,@NonNull final AuthenticationCallback authenticationCallback) {
        if(!BiometricUtils.isSdkVersionSupported()) {
            authenticationCallback.onSdkVersionNotSupported();
            return;
        }

        if(!BiometricUtils.isPermissionGranted(context)) {
            authenticationCallback.onBiometricAuthenticationPermissionNotGranted();
            return;
        }

        if(!BiometricUtils.isHardwareSupported(context)) {
            authenticationCallback.onBiometricAuthenticationNotSupported();
            return;
        }

        if(!BiometricUtils.hasEnrolledFingerprints(context)) {
            authenticationCallback.onBiometricAuthenticationNotAvailable();
            return;
        }

        //displayBiometricDialog(biometricCallback);
        authenticateWithApiHandler(context, authenticationCallback);

    }

    /**
     * Cancel the pending authentication
     */
    public void cancelAuthentication(){
        if(apiHandler != null){
            apiHandler.cancelAuthentication();
        }
    }



    private void authenticateWithApiHandler(Context context, AuthenticationCallback authenticationCallback) {
        this.apiHandler = createAndSetupApiHandler();
        this.apiHandler.authenticate(context, authenticationCallback);
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

    private void setupApiHandler(AbstractApiHandler abstractApiHandler, Builder biometricBuilder) {
        abstractApiHandler.setTitle(biometricBuilder.title);
        abstractApiHandler.setSubtitle(biometricBuilder.subtitle);
        abstractApiHandler.setDescription(biometricBuilder.description);
        abstractApiHandler.setNegativeButtonText(biometricBuilder.negativeButtonText);
        abstractApiHandler.setAuthenticationPurpose(biometricBuilder.authenticationPurpose);
        abstractApiHandler.setCryptoParams(biometricBuilder.cryptoParams);
    }


    public static Builder newBuilder(){
        return new Builder();
    }

    /**
     * Builder class
     */
    public static class Builder {

        private String title;
        private String subtitle;
        private String description;
        private String negativeButtonText;
        private AuthenticationPurpose authenticationPurpose;
        private CryptoParams cryptoParams;

        private Builder() {

        }

        /**
         * Set the title of the sheet dialog
         * @param title the title
         * @return the builder
         */
        public Builder setTitle(@NonNull final String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the subtitle of the sheet dialog
         * @param subtitle the subtitle
         * @return the builder
         */
        public Builder setSubtitle(@NonNull final String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        /**
         * Set a description message
         * @param description the description
         * @return the builder
         */
        public Builder setDescription(@NonNull final String description) {
            this.description = description;
            return this;
        }


        /**
         * Set the negative button text
         * @param negativeButtonText the button text
         * @return the builder
         */
        public Builder setNegativeButtonText(@NonNull final String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        /**
         * Set the authentication purpose. This parameter depending on your needs
         * @param authenticationPurpose
         * @return the builder
         */
        public Builder setAuthenticationPurpose(@NonNull AuthenticationPurpose authenticationPurpose) {
            this.authenticationPurpose = authenticationPurpose;
            return this;
        }

        /**
         * Set the crypto params to use inside the crypto layer
         * @param cryptoParams the crypto params
         * @return the builder
         */
        public Builder setCryptoParams(CryptoParams cryptoParams) {
            this.cryptoParams = cryptoParams;
            return this;
        }

        /**
         * Build a new {@link BiometricAuthenticator} instance
         * @return the new instance
         */
        public BiometricAuthenticator build() throws IllegalArgumentException {
            checkParameters();
            return new BiometricAuthenticator(this);
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
