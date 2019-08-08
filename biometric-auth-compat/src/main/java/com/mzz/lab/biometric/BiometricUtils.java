package com.mzz.lab.biometric;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;


/**
 * Utils for biometric conditions management
 */
public class BiometricUtils {

    private BiometricUtils(){}

    /**
     * Check if system biometric prompt is enabled
     * @return true if available, false otherwise
     */
    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }

    /**
     * Check if current api level support biometric api
     * @return
     */
    public static boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }


    /**
     * Check hardware support
     * @param context the context
     * @return true if hardware hass been detected, false otherwise
     */
    public static boolean isHardwareSupported(Context context) {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if(fingerprintManager == null){
                return false;
            }
            return fingerprintManager.isHardwareDetected();
        }
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }


    /**
     * Check if almost a fingerprint is available
     * @param context
     * @return
     */
    public static boolean hasEnrolledFingerprints(Context context) {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if(fingerprintManager == null){
                return false;
            }
            return fingerprintManager.hasEnrolledFingerprints();
        }
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }


    /**
     * Check if fingerprint permission has been granted
     * @param context the android context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
