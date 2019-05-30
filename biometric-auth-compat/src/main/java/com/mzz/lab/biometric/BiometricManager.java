package com.mzz.lab.biometric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mzz.lab.biometric.internal.api.AbstractApiHandler;
import com.mzz.lab.biometric.internal.api.BiometricApiHandler;
import com.mzz.lab.biometric.internal.api.FingerprintCompatApiHandler;

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
        /*if(BiometricUtils.isBiometricPromptEnabled()) {
            displayBiometricPrompt(biometricCallback);
        } else {
            displayBiometricPromptV23(biometricCallback);
        }*/
        this.apiHandler = createApiHandler();
        this.apiHandler.authenticate(context,biometricCallback);
    }

    private AbstractApiHandler createApiHandler() {
        AbstractApiHandler abstractApiHandler;
        if(BiometricUtils.isBiometricPromptEnabled()){
            abstractApiHandler = new BiometricApiHandler();
        }else{
            abstractApiHandler = new FingerprintCompatApiHandler();
        }

        abstractApiHandler.setTitle(biometricBuilder.title);
        abstractApiHandler.setSubtitle(biometricBuilder.subtitle);
        abstractApiHandler.setDescription(biometricBuilder.description);
        abstractApiHandler.setNegativeButtonText(biometricBuilder.negativeButtonText);

        return abstractApiHandler;
    }


    /*
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
    */


    public static class BiometricBuilder {

        private String title;
        private String subtitle;
        private String description;
        private String negativeButtonText;

        public BiometricBuilder() {

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
        }
    }
}
