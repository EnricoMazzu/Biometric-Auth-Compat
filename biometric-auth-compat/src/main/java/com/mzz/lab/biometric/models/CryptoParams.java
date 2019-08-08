package com.mzz.lab.biometric.models;

import javax.crypto.spec.IvParameterSpec;

/**
 * Class that contains crypto parameters like iv, generated key name and key invalidation policy
 */
public class CryptoParams {
    private String keyName;
    private boolean deleteAfterInvalidation;
    private byte[] iv;

    private CryptoParams(Builder builder) {
        this.keyName = builder.keyName;
        this.deleteAfterInvalidation = builder.deleteAfterInvalidation;
        this.iv = builder.iv;
    }

    /**
     * Get the key name
     * @return the key name
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Check if the key should be deleted after invalidation
     * @return
     */
    public boolean isDeleteAfterInvalidation() {
        return deleteAfterInvalidation;
    }

    /**
     * The iv vector as byte array
     * @return the iv byte array
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * Get the ivParameterSpec created from the iv or null if iv is null
     * @return the ivParameterSpec or null
     */
    public IvParameterSpec getIvParameterSpec(){
        if(iv == null){
            return null;
        }
        return new IvParameterSpec(iv);
    }

    /**
     * Create a newBuilder
     * @param keyName the key name (aka alias) used to identify the key in the Key management system
     * @return a new builder instance
     */
    public static Builder newBuilder(String keyName){
        return new Builder(keyName);
    }

    /**
     * Builder class for {@link CryptoParams}
     */
    public static class Builder {
        private String keyName;
        private boolean deleteAfterInvalidation;
        private byte[] iv;

        /**
         * Create the builder with the key name
         * @param keyName
         */
        public Builder(String keyName) {
            this.keyName = keyName;
        }

        /**
         * Set if delete the key after that {@link android.security.keystore.KeyPermanentlyInvalidatedException} has been thrown
         * @param deleteAfterInvalidation the value (true to delete, false otherwise
         * @return the builder
         */
        public Builder setDeleteAfterInvalidation(boolean deleteAfterInvalidation) {
            this.deleteAfterInvalidation = deleteAfterInvalidation;
            return this;
        }

        /**
         * Set the iv to use
         * @param iv the iv byte array
         * @return the builder
         */
        public Builder setIv(byte[] iv) {
            this.iv = iv;
            return this;
        }

        /**
         * Build the new {@link CryptoParams} instance
         * @return the CryptoParams instance
         */
        public CryptoParams build() {
            return new CryptoParams(this);
        }
    }
}
