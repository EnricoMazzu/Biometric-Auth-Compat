package com.mzz.lab.biometric.models;

/**
 * Enum that define the purpose when you use a BiometricManager instance
 */
public enum AuthenticationPurpose {
    /**
     * Authenticate the user with biometric without create or init crypto entities
     * After the authentication, you should be able to use your own crypto entities as you want
     */
    NONE,

    /**
     * Authenticate the user with biometric and init the crypto layer for encrypt flow. When the authentication successfully done,
     * you receive a {@link CryptoEntity} that contains crypto objects (like {@link javax.crypto.Cipher} to
     * perform the encryption of your sensitive data
     */
    ENCRYPT,

    /**
     * Authenticate the user with biometric and init the crypto layer for decrypt flow. When the authentication successfully done,
     * you receive a {@link CryptoEntity} that contains crypto objects (like {@link javax.crypto.Cipher} to
     * perform the decrypt of your encrypted data
     */
    DECRYPT
}
