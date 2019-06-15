package com.mzz.lab.biometric.models;

import javax.crypto.spec.IvParameterSpec;

public class CryptoParams {
    private String keyName;
    private boolean deleteAfterInvalidation;
    private byte[] iv;

    private CryptoParams(Builder builder) {
        this.keyName = builder.keyName;
        this.deleteAfterInvalidation = builder.deleteAfterInvalidation;
        this.iv = builder.iv;
    }

    public String getKeyName() {
        return keyName;
    }

    public boolean isDeleteAfterInvalidation() {
        return deleteAfterInvalidation;
    }

    public byte[] getIv() {
        return iv;
    }

    public IvParameterSpec getIvParameterSpec(){
        if(iv == null){
            return null;
        }
        return new IvParameterSpec(iv);
    }

    public static Builder newBuilder(String keyName){
        return new Builder(keyName);
    }

    public static class Builder {
        private String keyName;
        private boolean deleteAfterInvalidation;
        private byte[] iv;

        public Builder(String keyName) {
            this.keyName = keyName;
        }

        public Builder setDeleteAfterInvalidation(boolean deleteAfterInvalidation) {
            this.deleteAfterInvalidation = deleteAfterInvalidation;
            return this;
        }

        public Builder setIv(byte[] iv) {
            this.iv = iv;
            return this;
        }

        public CryptoParams build() {
            return new CryptoParams(this);
        }
    }
}
