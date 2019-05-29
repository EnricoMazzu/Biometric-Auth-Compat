package com.mzz.lab.biometric;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.mzz.lab.biometric.internal.BiometricResultFactory;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;



@TargetApi(Build.VERSION_CODES.M)
public abstract class BiometricManagerV23 {

    private static final String KEY_NAME = UUID.randomUUID().toString();

    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;

    protected Context context;

    protected String title;
    protected String subtitle;
    protected String description;
    protected String negativeButtonText;
    private BiometricDialogV23 biometricDialogV23;

    protected CancellationDelegate cancellationDelegate;

    public void displayBiometricPromptV23(final BiometricCallback biometricCallback) {
        generateKey();
        if(initCipher()) {
            if(useCompat()){
                setupWithCompat(biometricCallback);
            }else{
                setupWithLegacy(biometricCallback);
            }
            displayBiometricDialog();
        }
    }

    private void setupWithLegacy(final BiometricCallback biometricCallback) {
        cancellationDelegate = new CancellationDelegateLegacy();
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if(fingerprintManager == null){
            biometricCallback.onBiometricAuthenticationInternalError("FingerprintManager is null");
            return;
        }

        fingerprintManager.authenticate(cryptoObject, (android.os.CancellationSignal) cancellationDelegate.get(), 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                updateStatus(String.valueOf(errString));
                if(errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED){
                    biometricCallback.onAuthenticationCancelled();
                }else{
                    biometricCallback.onAuthenticationError(errorCode, errString);
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                updateStatus(String.valueOf(helpString));
                biometricCallback.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                dismissDialog();
                biometricCallback.onAuthenticationSuccessful(BiometricResultFactory.from(result));
            }

            @Override
            public void onAuthenticationFailed() {
                updateStatus(context.getString(R.string.biometric_failed));
                biometricCallback.onAuthenticationFailed();
            }
        },null);
    }

    private boolean useCompat() {
        return !(Build.VERSION.SDK_INT == Build.VERSION_CODES.M);
    }


    private void setupWithCompat(final BiometricCallback biometricCallback) {
        cancellationDelegate = new CancellationDelegateCompat();
        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);

        fingerprintManagerCompat.authenticate(cryptoObject, 0, (CancellationSignal) cancellationDelegate.get(),
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        updateStatus(String.valueOf(errString));
                        if(errMsgId == FingerprintManager.FINGERPRINT_ERROR_CANCELED){
                            biometricCallback.onAuthenticationCancelled();
                        }else{
                            biometricCallback.onAuthenticationError(errMsgId, errString);
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        updateStatus(String.valueOf(helpString));
                        biometricCallback.onAuthenticationHelp(helpMsgId, helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        dismissDialog();
                        biometricCallback.onAuthenticationSuccessful(BiometricResultFactory.from(result));
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        updateStatus(context.getString(R.string.biometric_failed));
                        biometricCallback.onAuthenticationFailed();
                    }
                }, null);
    }


    private void displayBiometricDialog() {
        biometricDialogV23 = new BiometricDialogV23(context,cancellationDelegate);
        biometricDialogV23.setTitle(title);
        //biometricDialogV23.setSubtitle(subtitle);
        biometricDialogV23.setDescription(description);
        biometricDialogV23.setButtonText(negativeButtonText);
        biometricDialogV23.show();
    }



    private void dismissDialog() {
        if(biometricDialogV23 != null) {
            biometricDialogV23.dismiss();
        }
    }

    private void updateStatus(String status) {
        if(biometricDialogV23 != null) {
            biometricDialogV23.updateStatus(status);
        }
    }

    private void generateKey() {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
        }
    }



    private boolean initCipher() {
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
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;


        } catch (KeyPermanentlyInvalidatedException e) {
            return false;

        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {

            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



    public abstract static class CancellationDelegate<T>{
        protected final T cancellationSignal;

        public CancellationDelegate(T cancellationSignal) { this.cancellationSignal = cancellationSignal; }

        public T get(){
            return cancellationSignal;
        }

        public abstract void cancel();
        public abstract boolean isCanceled();
    }


    protected static class CancellationDelegateLegacy extends CancellationDelegate<android.os.CancellationSignal>{
        public CancellationDelegateLegacy() {
            super(new android.os.CancellationSignal());
        }

        @Override
        public void cancel() {
            cancellationSignal.cancel();
        }

        @Override
        public boolean isCanceled() {
            return cancellationSignal.isCanceled();
        }
    }


    protected static class CancellationDelegateCompat extends CancellationDelegate<CancellationSignal>{

        private CancellationDelegateCompat(){
            super(new CancellationSignal());
        }

        @Override
        public void cancel() {
            cancellationSignal.cancel();
        }

        @Override
        public boolean isCanceled() {
            return cancellationSignal.isCanceled();
        }
    }

}
