package com.mzz.lab.biometric.internal.crypto;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import com.mzz.lab.biometric.models.CryptoParams;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CryptoContext{

    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;


    public CryptoContext(CryptoParams cryptoParams) throws CryptoContextInitException {
        try {
            String keyName = cryptoParams.getKeyName();
            initKeystore();
            if(!isKeyPresent(keyName)){
                generateKey(keyName);
            }
            boolean invalidated = initCipher(keyName);
            if(invalidated){
                deleteKeyByAlias(keyName);
                throw new InvalidatedKeyException();
            }
        }catch (Exception ex){
            throw new CryptoContextInitException(ex);
        }

    }

    private void deleteKeyByAlias(String keyName) throws KeyStoreException {
        keyStore.deleteEntry(keyName);
    }

    private void initKeystore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
    }

    private boolean isKeyPresent(String keyName) {
        try {
            return keyStore.containsAlias(keyName);
        }catch (Exception ex){
            return false;
        }
    }

    public Cipher getCipher() {
        return cipher;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey(String keyName) {
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException exc) {
           throw new RuntimeException(exc);
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCipher(String keyName) {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(keyName,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;

        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {

            throw new RuntimeException("Failed to startAuthentication Cipher", e);
        }
    }
}
