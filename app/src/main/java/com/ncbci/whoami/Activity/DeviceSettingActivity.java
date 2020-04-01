package com.ncbci.whoami.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ncbci.whoami.R;
import com.ncbci.whoami.service.BluetoothChatService;
import com.ncbci.whoami.service.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceSettingActivity extends AppCompatActivity {

    private List<ScanResult> wifilist;
    private ArrayList<String> wifis;
    private ArrayAdapter<String> adapter;
    private ProgressBar bleProgress;
    private ImageView okImage;
    private WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bleProgress = findViewById(R.id.ble_progress);
        okImage = findViewById(R.id.okImage);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if(success){
                    scanSuccess();
                }else{
                    //Toast.makeText(getActivity(),"scan failed",Toast.LENGTH_SHORT).show();
                }
            }
        };

        if(!wifi.isWifiEnabled()){
            wifi.setWifiEnabled(true);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifi.startScan();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bleProgress.setVisibility(View.INVISIBLE);
                okImage.setVisibility(View.VISIBLE);


                final Dialog dialog = new Dialog(DeviceSettingActivity.this);
                dialog.setContentView(R.layout.dialog_design);
                Button dialog_btn2 = dialog.findViewById(R.id.configure);
                Spinner dialog_spinner = dialog.findViewById(R.id.all_wifi);
                EditText dialog_password = dialog.findViewById(R.id.password);
                if(!wifi.isWifiEnabled()){
                    wifi.setWifiEnabled(true);
                }
                wifi.startScan();
                // get all avaliable wifi ssid
                wifis = new ArrayList<>();
                wifilist = scanSuccess();
                for(int i = 0; i < wifilist.size(); i++){
                    wifis.add(((wifilist.get(i).SSID)));
                }
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, wifis);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialog_spinner.setAdapter(adapter);
                dialog_btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "設定成功！！！", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DeviceSettingActivity.this, MainActivity.class));
                        finish();
                    }
                });
                dialog.show();
            }
        }, 5000);
    }
    List<ScanResult> scanSuccess(){
        List<ScanResult> results = wifi.getScanResults();
        return results;
    }
}
