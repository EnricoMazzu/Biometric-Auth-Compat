package com.mzz.lab.biometric.internal.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mzz.lab.biometric.R;
import com.mzz.lab.biometric.internal.CancellationDelegate;

public class BiometricDialogV23 extends BottomSheetDialog implements View.OnClickListener {

    private final CancellationDelegate cancellationDelegate;

    private Button btnCancel;
    private TextView itemTitle, itemDescription, /*itemSubtitle,*/ itemStatus;


    public BiometricDialogV23(@NonNull Context context, final CancellationDelegate cancellationDelegate) {
        super(context, R.style.BottomSheetDialogTheme);
        this.cancellationDelegate = cancellationDelegate;
        setDialogView();
        setOnDismissListener(new OnDismissWithCancellationImpl(cancellationDelegate));
    }

    private void setDialogView() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fingerprint_bottom_sheet, null);
        setContentView(bottomSheetView);

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        itemTitle = findViewById(R.id.item_title);
        itemStatus = findViewById(R.id.item_status);
        itemDescription = findViewById(R.id.item_description);

    }

    public void setTitle(String title) {
        itemTitle.setText(title);
    }

    public void updateStatus(String status) {
        itemStatus.setText(status);
    }

    /*public void setSubtitle(String subtitle) {
        itemSubtitle.setText(subtitle);
    }*/

    public void setDescription(String description) {
        itemDescription.setText(description);
    }

    public void setButtonText(String negativeButtonText) {
        btnCancel.setText(negativeButtonText);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        cancellationDelegate.cancel();
    }



    private static class OnDismissWithCancellationImpl implements OnDismissListener{
        private CancellationDelegate<?> cancellationDelegate;

        public OnDismissWithCancellationImpl(CancellationDelegate<?> cancellationDelegate) {
            this.cancellationDelegate = cancellationDelegate;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            cancellationDelegate.cancel();
        }
    }
}
