package com.mzz.lab.biometric.internal.api.fingerprint;

import android.content.Context;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.BiometricResultFactory;
import com.mzz.lab.biometric.internal.CancellationDelegate;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;

import java.lang.ref.WeakReference;

public class FingerprintCompatApiHandler extends FingerprintApiHandler {

    @Override
    protected void init(Context context, BiometricCallback biometricCallback) {
        if(useCompat()){
            setupWithCompat(context,biometricCallback);
        }else{
            super.init(context,biometricCallback);
        }
    }


    private boolean useCompat() {
        return !(Build.VERSION.SDK_INT == Build.VERSION_CODES.M);
    }


    private void setupWithCompat(Context context, final BiometricCallback biometricCallback) {
        cancellationDelegate = new CancellationDelegateCompat();
        CryptoContext cryptoContext = getCryptoContext();
        if(cryptoContext == null){
            //TODO send specific error;
            biometricCallback.onBiometricAuthenticationInternalError("invalid crypto context");
            return;
        }
        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cryptoContext.getCipher());
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);


        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        fingerprintManagerCompat.authenticate(cryptoObject, 0, (CancellationSignal) cancellationDelegate.get(),
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        handleOnAuthenticationError(errMsgId,errString,biometricCallback);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        handleOnAuthenticationHelp(helpMsgId,helpString,biometricCallback);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        handleOnAuthenticationSucceeded(BiometricResultFactory.from(result), biometricCallback);
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Context context = contextWeakReference.get();
                        handleOnAuthenticationFailed(context,biometricCallback);
                    }
                }, null);

        displayBiometricDialog(context);
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
