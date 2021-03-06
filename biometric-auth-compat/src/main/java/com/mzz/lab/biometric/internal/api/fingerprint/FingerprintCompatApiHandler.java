package com.mzz.lab.biometric.internal.api.fingerprint;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.mzz.lab.biometric.AuthenticationCallback;
import com.mzz.lab.biometric.internal.BiometricResultFactory;
import com.mzz.lab.biometric.internal.CancellationDelegate;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.models.errors.CryptoContextInitException;

import java.lang.ref.WeakReference;

public class FingerprintCompatApiHandler extends FingerprintApiHandler {

    @Override
    protected void startAuthentication(Context context, AuthenticationCallback authenticationCallback) throws CryptoContextInitException {
        if(useCompat()){
            setupWithCompat(context, authenticationCallback);
        }else{
            super.startAuthentication(context, authenticationCallback);
        }
    }


    private boolean useCompat() {
        return !(Build.VERSION.SDK_INT == Build.VERSION_CODES.M);
    }


    private void setupWithCompat(Context context, final AuthenticationCallback authenticationCallback) throws CryptoContextInitException {
        cancellationDelegate = new CancellationDelegateCompat();
        CryptoContext cryptoContext = getCryptoContext();
        FingerprintManagerCompat.CryptoObject cryptoObject = toCryptoObject(cryptoContext);
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);

        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        fingerprintManagerCompat.authenticate(cryptoObject, 0, (CancellationSignal) cancellationDelegate.get(),
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        handleOnAuthenticationError(errMsgId,errString, authenticationCallback);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        handleOnAuthenticationHelp(helpMsgId,helpString, authenticationCallback);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        handleOnAuthenticationSucceeded(BiometricResultFactory.from(result), authenticationCallback);
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Context context = contextWeakReference.get();
                        handleOnAuthenticationFailed(context, authenticationCallback);
                    }
                }, null);

        displayBiometricDialog(context);
    }

    @Nullable
    private FingerprintManagerCompat.CryptoObject toCryptoObject(CryptoContext cryptoContext) {
        if(cryptoContext == null){
            return null;
        }
        return new FingerprintManagerCompat.CryptoObject(cryptoContext.getCipher());
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
