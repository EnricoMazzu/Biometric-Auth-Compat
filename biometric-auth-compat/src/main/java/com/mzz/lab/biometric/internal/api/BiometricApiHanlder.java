package com.mzz.lab.biometric.internal.api;

import com.mzz.lab.biometric.BiometricCallback;

public abstract class BiometricApiHanlder {

    public BiometricApiHanlder(BiometricCallback biometricCallback){

    }

    protected abstract void init();


    public abstract static class CancellationDelegate<T>{
        protected final T cancellationSignal;

        public CancellationDelegate(T cancellationSignal) { this.cancellationSignal = cancellationSignal; }

        public T get(){
            return cancellationSignal;
        }

        public abstract void cancel();
        public abstract boolean isCanceled();
    }

}
