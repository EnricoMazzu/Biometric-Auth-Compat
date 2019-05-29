package com.mzz.lab.biometric.internal.api;

import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.BiometricManagerV23;
import com.mzz.lab.biometric.R;
import com.mzz.lab.biometric.internal.BiometricResultFactory;
import com.mzz.lab.biometric.internal.CancellationDelegate;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;

public class FingerprintCompatApiHandler extends FingerprintApiHandler {


    @Override
    protected void init(BiometricCallback biometricCallback) {
        setupWithCompat(biometricCallback);
    }

    private void setupWithCompat(final BiometricCallback biometricCallback) {
        cancellationDelegate = new CancellationDelegateCompat();
        CryptoContext cryptoContext = getCryptoContext();
        if(cryptoContext == null){
            //TODO send specific error;
            biometricCallback.onBiometricAuthenticationInternalError("invalid crypto context");
            return;
        }
        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cryptoContext.getCipher());
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

        displayBiometricDialog();
    }


    protected static class CancellationDelegateCompat extends CancellationDelegate<CancellationSignal> {

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
