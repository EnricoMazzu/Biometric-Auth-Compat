package com.mzz.lab.biometric.models;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

/**
 * Class that contains crypto object that you can use to perform crypto operations with JCA APIs
 */
public class CryptoEntity {
    private Mac mac;
    private Signature signature;
    private Cipher cipher;

    private CryptoEntity(){}

    /**
     * Get the cipher instance
     * @return a cipher instance
     */
    public Cipher getCipher() {
        return cipher;
    }

    /**
     * Get the Mac instance
     * @return the Mac
     */
    public Mac getMac() {
        return mac;
    }

    /**
     * Get the Signature instance
     * @return the Signature
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Create new builder
     * @return the new builder
     */
    public static Builder newBuilder(){
        return new Builder();
    }


    /**
     * Builder class for {@link CryptoEntity}
     */
    public static class Builder{
        private Mac mac;
        private Signature signature;
        private Cipher cipher;
        public Builder(){ }

        public Builder setMac(Mac mac) {
            this.mac = mac;
            return this;
        }

        public Builder setCipher(Cipher cipher) {
            this.cipher = cipher;
            return this;
        }

        public Builder setSignature(Signature signature) {
            this.signature = signature;
            return this;
        }

        public CryptoEntity build(){
            CryptoEntity cryptoEntity = new CryptoEntity();
            cryptoEntity.cipher = cipher;
            cryptoEntity.mac = mac;
            cryptoEntity.signature = signature;
            return cryptoEntity;
        }

    }
}
