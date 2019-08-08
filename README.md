# Biometric-Auth-Compat

## Project purpose

The goal of this project is provide a very easy to use (but useful) library in order to support biometric authentication across different Android api levels (from android 23 to Android 29) without requires Androidx


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
https://github.com/anitaa1990/Biometric-Auth-Sample , but with review and a complete re-design (in terms of api)


| Api level 23 | Api level 26 | Api Level 28 (biometricPrompt) |
|--------------|--------------|--------------------------------|
|     ![alt text](https://raw.githubusercontent.com/EnricoMazzu/Biometric-Auth-Compat/develop/images/api_level_23.png)         | ![alt text](https://raw.githubusercontent.com/EnricoMazzu/Biometric-Auth-Compat/develop/images/api_level_26.png)             |    ![alt text](https://raw.githubusercontent.com/EnricoMazzu/Biometric-Auth-Compat/develop/images/api_level_28.png) |


## Getting started
### Add repo and dependencies

//TODO

### Create BiometricAuthenticator
In order to use the Biometric authentication api, you need a BiometricManager instance.
To create it, use a newBuilder() methods to get a builder, configure your needs and call build() to build
a new manager instance.

```Java

private void authenticate(final AuthenticationPurpose authenticationPurpose) {

    // params value is mandatory only if authenticationPurpose != AuthenticationPurpose.NONE
    CryptoParams params = getCryptoParams(authenticationPurpose);


    BiometricAuthenticator authenticator = BiometricAuthenticator.newBuilder()
            .setTitle("Verification")
            .setSubtitle("")
            .setDescription("Confirm your identity to pay")
            .setNegativeButtonText("Cancel")
            .setCryptoParams(params)
            .setAuthenticationPurpose(authenticationPurpose)
            .build();

    setStatusText("OnAuthenticationPending");

    startAuthentication(authenticationPurpose, manager);
}


private CryptoParams getCryptoParams(AuthenticationPurpose authenticationPurpose) {
    if(authenticationPurpose == null || authenticationPurpose == AuthenticationPurpose.NONE){
        return null;
    }

    byte[] iv = null;

    if(authenticationPurpose == AuthenticationPurpose.ENCRYPT){
        // clear current encrypted data
        clearPinData();
        // generate a random secure iv
        iv = generateIV();
    }else if(authenticationPurpose == AuthenticationPurpose.DECRYPT){
        // encryptedSecretData is a simple wrapper class that contains encrypted data (base64 encoded)
        // and iv data(base64 encoded)
        iv = Base64.decode(encryptedSecretData.getBase64Iv(),Base64.NO_WRAP);
    }

    return CryptoParams.newBuilder(MY_KEY_ALIAS)
            .setDeleteAfterInvalidation(true)
            .setIv(iv)
            .build();
}

```

### Authenticate User

```Java
private void startAuthentication(final AuthenticationPurpose authenticationPurpose, BiometricAuthenticator authenticator) {
    // authenticate the user with the configured manager
    authenticator.authenticate(this, new AuthenticationCallback() {
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
### Use Crypto Entity

In the following sample you can see how to use authentication result with JCA

```Java

private void applyCrypto(@NonNull AuthenticationPurpose authenticationPurpose, BiometricAuthenticationResult authenticationResult) {
    if(authenticationPurpose == AuthenticationPurpose.ENCRYPT){
        encryptPin(authenticationResult);
    }else{
        decryptPin(encryptedSecretData,authenticationResult);
    }
}

private void encryptPin(BiometricAuthenticationResult authenticationResult) {
    try {
        String secret = "this is my secret pin";
        Cipher cipher = authenticationResult.getCryptoEntity().getCipher();
        byte[] encryptedData = cipher.doFinal(secret.getBytes());
        encryptedSecretData = new EncryptedData(getBase64Data(encryptedData),getBase64Data(cipher.getIV()));
        setStatusText("Encrypt Success");
    }catch (Exception ex){
        Log.e(LOG_TAG,"[ENC_PIN] encryptPin fail: " + ex.getMessage(),ex);
        setStatusText("Encrypt Fail");
    }
}


private String decryptPin(@NonNull EncryptedData encryptedPinData, @NonNull BiometricAuthenticationResult authenticationResult) {
    try {
        String data = encryptedPinData.getBase64Data();
        Cipher cipher = authenticationResult.getCryptoEntity().getCipher();
        byte[] decryptedData = cipher.doFinal(fromBase64(data));
        setStatusText("Decrypt Success");
        return new String(decryptedData);
    }catch (Exception ex){
        Log.e(LOG_TAG,"[ENC_PIN] encryptPin fail: " + ex.getMessage(),ex);
        setStatusText("Decrypt Fail");
        return null;
    }
}

@NonNull
private static String getBase64Data(@NonNull byte[] data) {
    return Base64.encodeToString(data,Base64.NO_WRAP);
}

@NonNull
private static byte[] fromBase64(@NonNull String dataStr){
    return Base64.decode(dataStr, Base64.NO_WRAP);
}

private void clearPinData() {
    encryptedSecretData = null;
}

private byte[] generateIV() {
    Random random = new SecureRandom();
    byte[] iv = new byte[16];
    random.nextBytes(iv);
    return iv;
}

```

### BiometricUtil (support APIs)
- isBiometricPromptEnabled(): Check if system biometric prompt is enabled
- isSdkVersionSupported() :  Check if current api level supports biometric api
- isHardwareSupported(...): Check hardware support
- hasEnrolledFingerprints(...): Check if almost a fingerprint is available
- isPermissionGranted(...): Check if fingerprint permission has been granted (static manifest permission)



## Project Roadmap
This is a work in progress
### milestone 0 (wip)
 - Define dialog view for api from 23 to 27 (with legacy compat library)
 - Use fingerprint or prompt api, depending on api lever.
 - Define and manage crypto entities, depending on authentication purpose.
 - Find a way to simplify the crypto operation and data transport
### milestone 1
To be defined
