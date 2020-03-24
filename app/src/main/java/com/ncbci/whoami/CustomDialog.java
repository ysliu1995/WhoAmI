package com.ncbci.whoami;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class CustomDialog implements DialogInterface.OnCancelListener, View.OnClickListener{

    private Context mContext;
    private Dialog mDialog;
    public Button getwifi,conf;
    public Spinner all_wifi;
    public EditText passwd;

    public CustomDialog(Context context){
        this.mContext = context;
    }

    public CustomDialog show(){
        mDialog = new Dialog(mContext, R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_design);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        // 點邊取消
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        getwifi = mDialog.findViewById(R.id.SSID);
        conf = mDialog.findViewById(R.id.configure);
        all_wifi = mDialog.findViewById(R.id.all_wifi);
        passwd = mDialog.findViewById(R.id.password);
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
