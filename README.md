# Biometric-Auth-Compat

## Project purpose
The goal of this project is provide a very easy to use but powerful library in order to support biometric authentication across different android api level (from android 23 to Android 29) without requires Androidx

### Intro
With jetpack, Google provide a library named androidx.biometric:
"*On devices running P and above, this will show a system-provided authentication prompt, using a device's supported biometric (fingerprint, iris, face, etc). On devices before P, this will show a dialog prompting for fingerprint authentication. The prompt will persist across orientation changes unless explicitly canceled by the client. For security reasons, the prompt will automatically dismiss when the activity is no longer in the foreground.*"

Sounds good... but androidx.biometric:
 - requires AndroidX in your project
 - it's currently an alpha (not ready for production)
 - It doesn't work well on android sdk 23

This project aims to have the same flexibility and features of androidx.biometric, but with the support of currently large used compat library (and in the next future the possibility to switch to android.x without changing your code).

N.B: We don't want to replace Google work, but we want only help developers to have a nice transition

References:
UI and callback originally inspired by
https://github.com/anitaa1990/Biometric-Auth-Sample.

## Getting started
### Add repo and dependencies

//TODO

### Use Biometric Api
In order to use the Biometric authentication api, you need a BiometricManager instance.
To create it, use a newBuilder() methods to get a builder, configure your needs and call build() to build
a new manager instance.



```Java

private void authenticate(final AuthenticationPurpose authenticationPurpose) {

    CryptoParams params = getCryptoParams(authenticationPurpose);


    BiometricManager manager = BiometricManager.newBuilder()
            .setTitle("Verification")
            .setSubtitle("")
            .setDescription("Confirm your identity to pay")
            .setNegativeButtonText("Cancel")
            .setCryptoParams(params)
            .setAuthenticationPurpose(authenticationPurpose)
            .build();

    setStatusText("OnAuthenticationPending");

    manager.authenticate(this,new BiometricCallback() {
        @Override
        public void onSdkVersionNotSupported() {
            setStatusText("onSdkVersionNotSupported");
        }

        @Override
        public void onBiometricAuthenticationNotSupported() {
            setStatusText("onBiometricAuthenticationNotSupported");
        }

        @Override
        public void onBiometricAuthenticationNotAvailable() {
            setStatusText("onBiometricAuthenticationNotAvailable");
        }

        @Override
        public void onBiometricAuthenticationPermissionNotGranted() {
            setStatusText("onBiometricAuthenticationPermissionNotGranted");
        }

        @Override
        public void onBiometricAuthenticationInternalError(CryptoContextInitException error) {
            setStatusText("onBiometricAuthenticationInternalError");
        }

        @Override
        public void onAuthenticationFailed() {
            setStatusText("onAuthenticationFailed");
        }

        @Override
        public void onAuthenticationCancelled() {
            setStatusText("onAuthenticationCancelled");
        }

        @Override
        public void onAuthenticationSuccessful(BiometricAuthenticationResult authenticationResult) {
            setStatusText("onAuthenticationSuccessful");
            if(authenticationPurpose == AuthenticationPurpose.NONE){
                return;
            }
            applyCrypto(authenticationPurpose,authenticationResult);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            setStatusText("onAuthenticationHelp");
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            setStatusText("onAuthenticationError " + errString);
        }
    });
}

```



## API
work in progress


## Roadmap
This is a work in progress
### milestone 0 (wip)
 - Define dialog view for api from 23 to 27 (with legacy compat library)
 - Use fingerprint or prompt api, depending on api lever.
 - Define and manage crypto entities, depending on authentication purpose.
 - Find a way to simplify the crypto operation and data transport
### milestone 1
To be defined
