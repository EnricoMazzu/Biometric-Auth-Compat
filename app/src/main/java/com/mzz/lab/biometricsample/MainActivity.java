package com.mzz.lab.biometricsample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mzz.lab.biometric.BiometricCallback;
import com.mzz.lab.biometric.BiometricManager;
import com.mzz.lab.biometric.models.AuthenticationPurpose;
import com.mzz.lab.biometric.models.CryptoParams;
import com.mzz.lab.biometric.models.errors.CryptoContextInitException;
import com.mzz.lab.biometric.models.BiometricAuthenticationResult;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {

    public static final String MY_KEY_ALIAS = "MyKey";
    private static final String LOG_TAG = "MainActivity";
    private Button btnAuthenticate;
    private Button btnEncrypt;
    private Button btnDecrypt;
    private TextView txtStatus;

    private EncryptedData encryptedSecretData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        btnAuthenticate = findViewById(R.id.btnAuthenticate);
        btnEncrypt = findViewById(R.id.btnEncrypt);
        btnDecrypt = findViewById(R.id.btnDecrypt);
        txtStatus = findViewById(R.id.txtStatus);

        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate(AuthenticationPurpose.NONE);
            }
        });

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate(AuthenticationPurpose.ENCRYPT);
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(encryptedSecretData == null){
                    showMessage("No encrypted data available");
                    return;
                }
                authenticate(AuthenticationPurpose.DECRYPT);
            }
        });


    }

    private void showMessage(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void authenticate(final AuthenticationPurpose authenticationPurpose) {

        CryptoParams params = getCryptoParams(authenticationPurpose);


        BiometricManager manager = BiometricManager.newBuilder()
                .setTitle("Verification")
                .setSubtitle("")
                .setDescription("Confirm your identity to pay")
                .setNegativeButtonText("Cancel")
                .setCryptoParams(params)
                .setAuthenticationPurpose(authenticationPurpose)
                .build();

        setStatusText("OnAuthenticationPending");

        manager.authenticate(this,new BiometricCallback() {
            @Override
            public void onSdkVersionNotSupported() {
                setStatusText("onSdkVersionNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotSupported() {
                setStatusText("onBiometricAuthenticationNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotAvailable() {
                setStatusText("onBiometricAuthenticationNotAvailable");
            }

            @Override
            public void onBiometricAuthenticationPermissionNotGranted() {
                setStatusText("onBiometricAuthenticationPermissionNotGranted");
            }

            @Override
            public void onBiometricAuthenticationInternalError(CryptoContextInitException error) {
                setStatusText("onBiometricAuthenticationInternalError");
            }

            @Override
            public void onAuthenticationFailed() {
                setStatusText("onAuthenticationFailed");
            }

            @Override
            public void onAuthenticationCancelled() {
                setStatusText("onAuthenticationCancelled");
            }

            @Override
            public void onAuthenticationSuccessful(BiometricAuthenticationResult authenticationResult) {
                setStatusText("onAuthenticationSuccessful");
                if(authenticationPurpose == AuthenticationPurpose.NONE){
                    return;
                }
                applyCrypto(authenticationPurpose,authenticationResult);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                setStatusText("onAuthenticationHelp");
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                setStatusText("onAuthenticationError " + errString);
            }
        });
    }

    private void setStatusText(String message) {
        txtStatus.setText(message);
    }

    private void applyCrypto(@NonNull AuthenticationPurpose authenticationPurpose, BiometricAuthenticationResult authenticationResult) {
        if(authenticationPurpose == AuthenticationPurpose.ENCRYPT){
            encryptPin(authenticationResult);
        }else{
            decryptPin(encryptedSecretData,authenticationResult);
        }
    }

    private void encryptPin(BiometricAuthenticationResult authenticationResult) {
        try {
            String secret = "this is my secret pin";
            Cipher cipher = authenticationResult.getCryptoEntity().getCipher();
            byte[] encryptedData = cipher.doFinal(secret.getBytes());
            encryptedSecretData = new EncryptedData(getBase64Data(encryptedData),getBase64Data(cipher.getIV()));
            setStatusText("Encrypt Success");
        }catch (Exception ex){
            Log.e(LOG_TAG,"[ENC_PIN] encryptPin fail: " + ex.getMessage(),ex);
            setStatusText("Encrypt Fail");
        }
    }


    private String decryptPin(@NonNull EncryptedData encryptedPinData, @NonNull BiometricAuthenticationResult authenticationResult) {
        try {
            String data = encryptedPinData.getBase64Data();
            Cipher cipher = authenticationResult.getCryptoEntity().getCipher();
            byte[] decryptedData = cipher.doFinal(fromBase64(data));
            setStatusText("Decrypt Success");
            return new String(decryptedData);
        }catch (Exception ex){
            Log.e(LOG_TAG,"[ENC_PIN]e ncryptPin fail: " + ex.getMessage(),ex);
            setStatusText("Decrypt Fail");
            return null;
        }
    }


    @NonNull
    private static String getBase64Data(@NonNull byte[] data) {
        return Base64.encodeToString(data,Base64.NO_WRAP);
    }

    @NonNull
    private static byte[] fromBase64(@NonNull String dataStr){
        return Base64.decode(dataStr,Base64.NO_WRAP);
    }

    private CryptoParams getCryptoParams(AuthenticationPurpose authenticationPurpose) {
        if(authenticationPurpose == null || authenticationPurpose == AuthenticationPurpose.NONE){
            return null;
        }

        byte[] iv = null;

        if(authenticationPurpose == AuthenticationPurpose.ENCRYPT){
            clearPinData();
            iv = generateIV();
        }else if(authenticationPurpose == AuthenticationPurpose.DECRYPT){
            iv = Base64.decode(encryptedSecretData.getBase64Iv(),Base64.NO_WRAP);
        }

        return CryptoParams.newBuilder(MY_KEY_ALIAS)
                    .setDeleteAfterInvalidation(true)
                    .setIv(iv)
                    .build();
    }

    private void clearPinData() {
        encryptedSecretData = null;
    }

    private byte[] generateIV() {
        Random random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }
}
