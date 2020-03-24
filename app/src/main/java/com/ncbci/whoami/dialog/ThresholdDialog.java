package com.ncbci.whoami.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncbci.whoami.LoginActivity;
import com.ncbci.whoami.MainActivity;
import com.ncbci.whoami.R;

public class ThresholdDialog implements DialogInterface.OnCancelListener, View.OnClickListener{

    private Context mContext;
    private Dialog mDialog;
    private String userId;

    public ThresholdDialog (Context context){
        this.mContext = context;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public ThresholdDialog show(){
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.threshold_dialog);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        SeekBar CO2 = mDialog.findViewById(R.id.CO2SeekBar);
        SeekBar PM = mDialog.findViewById(R.id.PMSeekBar);
        final TextView CO2Text = mDialog.findViewById(R.id.CO2);
        final TextView PMText = mDialog.findViewById(R.id.PM);

        CO2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CO2Text.setText("CO2 (" + progress + ")");
                DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                mdatabase.child("Users").child(userId).child("threshold").child("CO2").setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PMText.setText("PM (" + progress + ")");
                DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                mdatabase.child("Users").child(userId).child("threshold").child("PM").setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 點邊取消
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(this);
        mDialog.show();

        return this;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        mDialog.dismiss();
    }
}

