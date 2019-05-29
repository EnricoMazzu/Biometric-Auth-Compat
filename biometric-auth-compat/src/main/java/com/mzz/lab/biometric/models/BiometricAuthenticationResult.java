package com.mzz.lab.biometric.models;

public class BiometricAuthenticationResult {
    private CryptoEntity cryptoEntity;

    public BiometricAuthenticationResult(CryptoEntity cryptoEntity){
        this.cryptoEntity = cryptoEntity;
    }

    public CryptoEntity getCryptoEntity() {
        return cryptoEntity;
    }
}
