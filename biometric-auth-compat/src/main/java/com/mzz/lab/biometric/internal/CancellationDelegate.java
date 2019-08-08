package com.mzz.lab.biometric.internal;

public abstract class CancellationDelegate<T>{
    protected final T cancellationSignal;

    public CancellationDelegate(T cancellationSignal) { this.cancellationSignal = cancellationSignal; }

    public T get(){
        return cancellationSignal;
    }

    public abstract void cancel();
    public abstract boolean isCanceled();
}
