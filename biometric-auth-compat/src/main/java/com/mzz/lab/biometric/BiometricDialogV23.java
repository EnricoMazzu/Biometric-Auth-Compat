package com.mzz.lab.biometric;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mzz.lab.biometric.internal.CancellationDelegate;

public class BiometricDialogV23 extends BottomSheetDialog implements View.OnClickListener {

    private final CancellationDelegate cancellationDelegate;
    //private Context context;

    private Button btnCancel;
    //private ImageView imgLogo;
    private TextView itemTitle, itemDescription, /*itemSubtitle,*/ itemStatus;


    public BiometricDialogV23(@NonNull Context context, final CancellationDelegate cancellationDelegate) {
        super(context, R.style.BottomSheetDialogTheme);
        //this.context = context.getApplicationContext();
        this.cancellationDelegate = cancellationDelegate;
        setDialogView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancellationDelegate.cancel();
            }
        });
    }

    private void setDialogView() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fingerprint_bottom_sheet, null);
        setContentView(bottomSheetView);

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        //imgLogo = findViewById(R.id.img_logo);
        itemTitle = findViewById(R.id.item_title);
        itemStatus = findViewById(R.id.item_status);
        //itemSubtitle = findViewById(R.id.item_subtitle);
        itemDescription = findViewById(R.id.item_description);

        //updateLogo();
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

    /*private void updateLogo() {
        try {
            Drawable drawable = getContext().getPackageManager().getApplicationIcon(context.getPackageName());
            imgLogo.setImageDrawable(drawable);
        } catch (Exception e) {
            Log.e("BiometricDialogV23",e.getMessage(),e);
        }
    }*/


    @Override
    public void onClick(View view) {
        dismiss();
        cancellationDelegate.cancel();
    }
}
