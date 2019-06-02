package com.mzz.lab.biometric.models;

public class CryptoParams {
    private String keyName;
    private boolean deleteAfterInvalidation;

    private CryptoParams(Builder builder) {
        this.keyName = builder.keyName;
        this.deleteAfterInvalidation = builder.deleteAfterInvalidation;
    }

    public String getKeyName() {
        return keyName;
    }

    public boolean isDeleteAfterInvalidation() {
        return deleteAfterInvalidation;
    }

    public static class Builder {
        private String keyName;
        private boolean deleteAfterInvalidation;

        public Builder(String keyName) {
            this.keyName = keyName;
        }

        public Builder setDeleteAfterInvalidation(boolean deleteAfterInvalidation) {
            this.deleteAfterInvalidation = deleteAfterInvalidation;
            return this;
        }

        public CryptoParams build() {
            return new CryptoParams(this);
        }
    }
}
