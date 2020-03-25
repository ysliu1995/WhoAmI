package com.ncbci.whoami.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncbci.whoami.R;
import com.ncbci.whoami.dialog.ProgressDialog;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    private EditText editEmail, editPassword;
    private Button loginBtn;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }


    private void initView(){
        editEmail = findViewById(R.id.editAccount);
        editPassword = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.loginBtn);
    }

    private void Login(){
        String email = "";
        String password = "";
        if(!editEmail.getText().toString().equals("")){
            email = editEmail.getText().toString();
        }else{
            Toast.makeText(LoginActivity.this, "信箱未填寫", Toast.LENGTH_SHORT).show();
        }
        if(!editPassword.getText().toString().equals("")){
            password = editPassword.getText().toString();
        }else{
            Toast.makeText(LoginActivity.this, "密碼未填寫", Toast.LENGTH_SHORT).show();
        }
        if((!email.equals("")) && (!password.equals(""))){
            ProgressDialog.showProgressDialog(LoginActivity.this);
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "登入成功!!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            Log.d(TAG, task.getException().getLocalizedMessage());
                            String mistake = task.getException().getLocalizedMessage();
                            if(mistake.equals("The email address is badly formatted.")){
                                Toast.makeText(LoginActivity.this, "信箱格式錯誤", Toast.LENGTH_SHORT).show();
                            }else if(mistake.equals("The password is invalid or the user does not have a password.")){
                                Toast.makeText(LoginActivity.this, "密碼錯誤", Toast.LENGTH_SHORT).show();
                            }else if(mistake.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("註冊")
                                .setMessage("尚未建立帳號, 是否立即建立?")
                                .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Register(editEmail.getText().toString(), editPassword.getText().toString());
                                        }
                                    })
                                .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                }).show();
                            }
                        }
                    }
                });
        }
    }
    private void Register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("streamURL", 0);
                        userInfo.put("fcmToken", 0);
                        userInfo.put("data/timestamp", 0);
                        userInfo.put("data/PM1", 0);
                        userInfo.put("data/PM2dot5", 0);
                        userInfo.put("data/PM10", 0);
                        userInfo.put("data/CO2", 0);
                        userInfo.put("data/temperature", 0);
                        userInfo.put("data/humidity", 0);
                        userInfo.put("threshold/PM", 0);
                        userInfo.put("threshold/CO2", 0);
                        mdatabase.child("Users").child(user.getUid()).updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        String mistake = task.getException().getLocalizedMessage();
                        Log.d(TAG, mistake);
                        if(mistake.equals("The email address is already in use by another account.")){
                            Toast.makeText(LoginActivity.this, "信箱已註冊, 請重新申請", Toast.LENGTH_SHORT).show();
                        }else if(mistake.equals("The given password is invalid. [ Password should be at least 6 characters ]")){
                            Toast.makeText(LoginActivity.this, "密碼長度過短, 需大於6位數", Toast.LENGTH_SHORT).show();
                        }else if(mistake.equals("The email address is badly formatted.")){
                            Toast.makeText(LoginActivity.this, "信箱格式錯誤", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        );
    }
}
