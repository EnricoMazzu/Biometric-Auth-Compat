package com.mzz.lab.biometric;

import android.content.Context;

import com.mzz.lab.biometric.models.errors.CryptoContextInitException;
import com.mzz.lab.biometric.models.BiometricAuthenticationResult;

/**
 * Callback interface used in {@link BiometricManager#authenticate(Context, BiometricCallback)}
 */
public interface BiometricCallback {
    /**
     * Called if sdk version not support biometric authentication
     */
    void onSdkVersionNotSupported();

    /**
     * Called if the device not support biometric authentication (probably because hardware is not present
     */
    void onBiometricAuthenticationNotSupported();

    /**
     * Called if device support biometric but there isn't biometry enrolled at this moment
     */
    void onBiometricAuthenticationNotAvailable();

    /**
     * Called when permissions aren't granted
     */
    void onBiometricAuthenticationPermissionNotGranted();

    /**
     * Called when an internal error has been generated (the common source of this errors is the illegal
     * use of the cryptography
     * @param error the error
     */
    void onBiometricAuthenticationInternalError(CryptoContextInitException error);

    /**
     * Notify that the authentication has been failed
     */
    void onAuthenticationFailed();

    /**
     * Notify that the authentication has been cancelled
     */
    void onAuthenticationCancelled();

    /**
     * Notify that the authentication successfully done. Depending on the {@link com.mzz.lab.biometric.models.AuthenticationPurpose} required,
     * the authenticationResult parameter could contain a {@link com.mzz.lab.biometric.models.CryptoEntity}, useful for crypto operations
     * @param authenticationResult
     */
    void onAuthenticationSuccessful(BiometricAuthenticationResult authenticationResult);

    /**
     * Called when a recoverable error has been encountered during authentication. The help
     * string is provided to give the user guidance for what went wrong, such as
     * "Sensor dirty, please clean it."
     * @param helpCode An integer identifying the error message
     * @param helpString A human-readable string that can be shown in UI
     */
    void onAuthenticationHelp(int helpCode, CharSequence helpString);

    /**
     * Called when an unrecoverable error has been encountered and the operation is complete.
     * No further callbacks will be made on this object.
     * @param errorCode An integer identifying the error message
     * @param errString A human-readable error string that can be shown in UI
     */
    void onAuthenticationError(int errorCode, CharSequence errString);
}
