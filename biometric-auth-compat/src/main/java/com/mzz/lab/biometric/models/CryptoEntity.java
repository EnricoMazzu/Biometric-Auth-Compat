package com.mzz.lab.biometric.models;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

public class CryptoEntity {
    private Mac mac;
    private Signature signature;
    private Cipher cipher;

    private CryptoEntity(){}

    public Cipher getCipher() {
        return cipher;
    }

    public Mac getMac() {
        return mac;
    }

    public Signature getSignature() {
        return signature;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

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
