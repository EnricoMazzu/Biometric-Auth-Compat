package com.mzz.lab.biometric.internal.crypto;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import com.mzz.lab.biometric.models.AuthenticationPurpose;
import com.mzz.lab.biometric.models.CryptoParams;
import com.mzz.lab.biometric.models.errors.CryptoContextInitException;

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

    public CryptoContext(CryptoParams cryptoParams, AuthenticationPurpose authenticationPurpose) throws CryptoContextInitException {
        try {
            String keyName = cryptoParams.getKeyName();
            initKeystore();
            if(!isKeyPresent(keyName)){
                generateKey(cryptoParams);
            }
            boolean valid = initCipher(cryptoParams,authenticationPurpose);
            if(!valid){
                if(cryptoParams.isDeleteAfterInvalidation()){
                    deleteKeyByAlias(keyName);
                }
                throw new InvalidatedKeyException();
            }
        }
        catch (CryptoContextInitException ex){
            //rethrow as is
            throw ex;
        }
        catch (Exception ex){
            // wrap in CryptoContextInitException
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
    private void generateKey(CryptoParams cryptoParams) throws CryptoContextInitException {
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(cryptoParams.getKeyName(), KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException exc) {
           throw new CryptoContextKeyGenException(exc);
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCipher(CryptoParams cryptoParams, AuthenticationPurpose authenticationPurpose) throws CryptoContextInitException {
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
            SecretKey key = (SecretKey) keyStore.getKey(cryptoParams.getKeyName(),
                    null);

            if(authenticationPurpose == AuthenticationPurpose.DECRYPT){
                cipher.init(getEncryptMode(authenticationPurpose), key, cryptoParams.getIvParameterSpec());
            }else{
                cipher.init(getEncryptMode(authenticationPurpose), key);
            }

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;

        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {

            throw new CryptoContextInitException(CryptoContextInitException.INIT_CIPHER_EXCEPTION, e);
        }
    }

    private int getEncryptMode(AuthenticationPurpose authenticationPurpose) {
        switch (authenticationPurpose){
            case ENCRYPT:
                return Cipher.ENCRYPT_MODE;
            case DECRYPT:
                return Cipher.DECRYPT_MODE;
            case NONE:
            default:
                throw new IllegalArgumentException("None purpose is invalid");
        }
    }
}
