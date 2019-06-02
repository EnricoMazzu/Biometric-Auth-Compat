package com.mzz.lab.biometricsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.BiometricManager;
import com.mzz.lab.biometric.internal.crypto.CryptoContextInitException;
import com.mzz.lab.biometric.models.BiometricAuthenticationResult;

public class MainActivity extends AppCompatActivity {

    private Button btnAuthenticate;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
    }

    private void bindView() {
        btnAuthenticate = findViewById(R.id.btnAuthenticate);
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });
        txtStatus = findViewById(R.id.txtStatus);
    }

    private void authenticate() {
        BiometricManager manager = BiometricManager.newBuilder()
                .setTitle("Verification")
                .setSubtitle("")
                .setDescription("Confirm your identity to pay")
                .setNegativeButtonText("Cancel")
                .build();

        txtStatus.setText("OnAuthenticationPending");

        manager.authenticate(this,new BiometricCallback() {
            @Override
            public void onSdkVersionNotSupported() {
                txtStatus.setText("onSdkVersionNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotSupported() {
                txtStatus.setText("onBiometricAuthenticationNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotAvailable() {
                txtStatus.setText("onBiometricAuthenticationNotAvailable");
            }

            @Override
            public void onBiometricAuthenticationPermissionNotGranted() {
                txtStatus.setText("onBiometricAuthenticationPermissionNotGranted");
            }

            @Override
            public void onBiometricAuthenticationInternalError(CryptoContextInitException error) {
                txtStatus.setText("onBiometricAuthenticationInternalError");
            }

            @Override
            public void onAuthenticationFailed() {
                txtStatus.setText("onAuthenticationFailed");
            }

            @Override
            public void onAuthenticationCancelled() {
                txtStatus.setText("onAuthenticationCancelled");
            }

            @Override
            public void onAuthenticationSuccessful(BiometricAuthenticationResult authenticationResult) {
                txtStatus.setText("onAuthenticationSuccessful");
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                txtStatus.setText("onAuthenticationHelp");
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                txtStatus.setText("onAuthenticationError " + errString);
            }
        });
    }


}
