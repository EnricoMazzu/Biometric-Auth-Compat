package com.mzz.lab.biometric.internal.api;

import android.content.Context;
import android.os.CancellationSignal;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.internal.CancellationDelegate;
import com.mzz.lab.biometric.internal.crypto.CryptoContext;
import com.mzz.lab.biometric.internal.crypto.CryptoContextInitException;

import java.util.UUID;

public abstract class AbstractApiHandler {

    private static final String KEY_NAME = UUID.randomUUID().toString();
    protected String title;
    protected String subtitle;
    protected String description;
    protected String negativeButtonText;

    protected Context context;

    protected CancellationDelegate cancellationDelegate;

    public AbstractApiHandler(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected abstract void init(BiometricCallback biometricCallback);

    protected CryptoContext getCryptoContext() {
        try {
            return new CryptoContext(KEY_NAME);
        } catch (CryptoContextInitException e) {
            return null;
        }
    }


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
