package com.ncbci.whoami.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncbci.whoami.R;

public class ClassRoomDialog implements DialogInterface.OnCancelListener, View.OnClickListener{

    private Context mContext;
    private Dialog mDialog;

    public ClassRoomDialog(Context context){
        this.mContext = context;
    }

    public ClassRoomDialog show(){
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.classroom_dialog);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView content = mDialog.findViewById(R.id.classContent);
        content.setText("Hello World");

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

