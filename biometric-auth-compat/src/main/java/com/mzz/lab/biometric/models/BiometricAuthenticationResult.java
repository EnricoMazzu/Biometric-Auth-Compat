package com.mzz.lab.biometric.models;

/**
 * Wrapper that could contains a valid {@link CryptoEntity}, depending on the desired {@link AuthenticationPurpose}
 */
public class BiometricAuthenticationResult {
    private CryptoEntity cryptoEntity;

    /**
     * Constructor
     * @param cryptoEntity the crypto entity
     */
    public BiometricAuthenticationResult(CryptoEntity cryptoEntity){
        this.cryptoEntity = cryptoEntity;
    }

    /**
     * Get the internal crypto entity instance
     * @return the crypto entity
     */
    public CryptoEntity getCryptoEntity() {
        return cryptoEntity;
    }
}
