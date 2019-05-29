package com.mzz.lab.biometric.internal.api;

import android.content.Context;
import android.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.CancellationDelegate;

public abstract class AbstractApiHandler {

    protected String title;
    protected String subtitle;
    protected String description;
    protected String negativeButtonText;

    protected Context context;

    protected CancellationDelegate cancellationDelegate;

    public AbstractApiHandler(){

    }

    protected abstract void init(BiometricCallback biometricCallback);


    protected static class CancellationDelegateLegacy extends CancellationDelegate<CancellationSignal> {
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


}
