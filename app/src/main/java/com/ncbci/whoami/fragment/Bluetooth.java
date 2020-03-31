package com.ncbci.whoami.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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

public class Bluetooth extends Fragment {
    private View v;
    private Button scanButton,wifiBtn,dialog_btn1,dialog_btn2;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 666;
    private static boolean isCountdown = false;
    private ProgressDialog mProgressDialog;
    private WifiManager wifi;
    private Spinner dialog_spinner;
    private EditText dialog_password;
    private List<ScanResult> wifilist;
    private ArrayList<String> wifis;
    private ArrayAdapter<String> adapter;
    private HashSet<String> h_adapter;
    private FirebaseAuth mAuth;
    int size;
    private ImageView okImage;
    private ProgressBar bleProgress;
    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivity";

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;
    private BluetoothChatService mChatService = null;
    private StringBuffer mOutStringBuffer;

    /**
     * Array adapter for the conversation thread
     */
    //private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, null);
        v = view;
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mChatService == null) {
            setupChat();
        }

        bleProgress = v.findViewById(R.id.ble_progress);
        okImage = v.findViewById(R.id.okImage);




        h_adapter = new HashSet<String>();
        mAuth = FirebaseAuth.getInstance();
        // check the location permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            }
        } else {
        }
        // check the location permission ----------------------------------------------------------------
        // check the location permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
        } else {
        }
        // check the location permission ----------------------------------------------------------------
        // check the bluetooth permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.BLUETOOTH_ADMIN)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 123);
            }
        } else {
        }
        // check the bluetooth permission ----------------------------------------------------------------
        // check the bluetooth permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.BLUETOOTH)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.BLUETOOTH},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, 123);
            }
        } else {
        }
        // check the bluetooth permission ----------------------------------------------------------------
        // check the wifi permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 123);
            }
        } else {
        }
        // check the wifi permission ----------------------------------------------------------------
        // check the wifi permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CHANGE_WIFI_STATE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 123);
            }
        } else {
        }
        // check the wifi permission ----------------------------------------------------------------
        wifiBtn = v.findViewById(R.id.wifi);
        scanButton = v.findViewById(R.id.scanDevice);
        wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifi.startScan();
        if(!success){
            //Toast.makeText(getActivity(),"scan failed",Toast.LENGTH_SHORT).show();
        }
        /*if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getActivity(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));*/

        wifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_design);
//                dialog_btn1 = (Button) dialog.findViewById(R.id.SSID);
                dialog_btn2 = (Button) dialog.findViewById(R.id.configure);
                dialog_spinner = (Spinner) dialog.findViewById(R.id.all_wifi);
                dialog_password = (EditText) dialog.findViewById(R.id.password);
                dialog_btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!wifi.isWifiEnabled()){
                            wifi.setWifiEnabled(true);
                        }
                        wifi.startScan();
                        // get all avaliable wifi ssid
                        wifis = new ArrayList<String>();
                        wifilist = scanSuccess();
                        for(int i = 0; i < wifilist.size(); i++){
                            wifis.add(((wifilist.get(i).SSID)));
                        }
                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, wifis);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dialog_spinner.setAdapter(adapter);
                    }
                });
                dialog_btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getActivity(),"send SSID : " + dialog_spinner.getSelectedItem().toString() + " PW: " + dialog_password.getText().toString(),Toast.LENGTH_SHORT).show();
//                        sendMessage(dialog_spinner.getSelectedItem().toString(),dialog_password.getText().toString(),mAuth.getUid());
                        getActivity().recreate();
                    }
                });
                dialog.show();
            }
        });
//        scanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkLocationPermission();
//                if (!mBtAdapter.isEnabled()) {
//                    mBtAdapter.enable();
//                }
//                // Initialize array adapters. One for already paired devices and
//                // one for newly discovered devices
//                doDiscovery();
//            }
//        });



        ArrayAdapter<String> pairedDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_name);


//        // Find and set up the ListView for paired devices
//        ListView pairedListView =  v.findViewById(R.id.all_device);
//        pairedListView.setAdapter(pairedDevicesArrayAdapter);
//        pairedListView.setOnItemClickListener(mDeviceClickListener);
//
//        // Find and set up the ListView for newly discovered devices
//        ListView newDevicesListView = v.findViewById(R.id.all_device2);
//        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
//        newDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Auto Turn on bluetooth
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        }

        checkLocationPermission();
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        }
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        doDiscovery();
//
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
//            v.findViewById(R.id.paired_device).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
//                Log.d(TAG, device.getName() + "\n" + device.getAddress());
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_design);
//                dialog_btn1 = (Button) dialog.findViewById(R.id.SSID);
                dialog_btn2 = dialog.findViewById(R.id.configure);
                dialog_spinner = (Spinner) dialog.findViewById(R.id.all_wifi);
                dialog_password = (EditText) dialog.findViewById(R.id.password);
//                dialog_btn1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(!wifi.isWifiEnabled()){
//                            wifi.setWifiEnabled(true);
//                        }
//                        wifi.startScan();
//                        // get all avaliable wifi ssid
//                        wifis = new ArrayList<String>();
//                        wifilist = scanSuccess();
//                        for(int i = 0; i < wifilist.size(); i++){
//                            wifis.add(((wifilist.get(i).SSID)));
//                        }
//                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, wifis);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        dialog_spinner.setAdapter(adapter);
//                    }
//                });
                if(!wifi.isWifiEnabled()){
                    wifi.setWifiEnabled(true);
                }
                wifi.startScan();
                // get all avaliable wifi ssid
                wifis = new ArrayList<String>();
                wifilist = scanSuccess();
                for(int i = 0; i < wifilist.size(); i++){
                    wifis.add(((wifilist.get(i).SSID)));
                }
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, wifis);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialog_spinner.setAdapter(adapter);
                dialog_btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getActivity(),"send SSID : " + dialog_spinner.getSelectedItem().toString() + " PW: " + dialog_password.getText().toString(),Toast.LENGTH_SHORT).show();
//                        sendMessage(dialog_spinner.getSelectedItem().toString(),dialog_password.getText().toString(),mAuth.getUid());
//                        Toast.makeText(getActivity(), "設定成功！！！", Toast.LENGTH_SHORT).show();
                        getActivity().recreate();
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

    void checkLocationPermission() {

        //check if we have ACCESS_COARSE_LOCATION permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //We don't have ACCESS_COARSE_LOCATION permission, request the permission.
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},666);
        }else{
            //we have permission
            doDiscovery();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                doDiscovery();

            }else{
                Toast.makeText(getActivity(), R.string.permission_declined, Toast.LENGTH_SHORT).show();
                scanButton.setVisibility(View.VISIBLE);
            }
        }else{
            //other request permission
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

    }

    private void stop(){
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        getActivity().unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");
        //scanButton.setVisibility(View.GONE);
        scanButton.setEnabled(false);
        // Indicate scanning in the title
        getActivity().setProgressBarIndeterminateVisibility(true);
        //getActivity().setTitle(R.string.scanning);

        // Turn on sub-title for new devices
//        v.findViewById(R.id.avaliable_device).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            //Intent intent = new Intent();
            //intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
            // TODO  after selected the device that we want to connect to
            // Get the BluetoothDevice object
            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            mChatService.connect(device, false);
        }
    };

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            bleProgress.setVisibility(View.INVISIBLE);
                            okImage.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    //isRead = false;
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "writeMessage = " + writeMessage);
                    //mConversationArrayAdapter.add("Command:  " + writeMessage);

                    break;
                case Constants.MESSAGE_READ:
                    //isRead = true;
//                    byte[] readBuf = (byte[]) msg.obj;
//                     construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    String readMessage = new String(readBuf);
                    String readMessage = (String)msg.obj;
                    Log.d(TAG, "readMessage = " + readMessage);
                    //TODO: if message is json -> callback from RPi
                    if(isJson(readMessage)){
                        handleCallback(readMessage);
                    }else{
                        if(isCountdown){
                            mHandler.removeCallbacks(watchDogTimeOut);
                            isCountdown = false;
                        }
                        if(mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                            Toast.makeText(activity, R.string.config_alreadyConfig, Toast.LENGTH_SHORT).show();
                        }
                        //remove the space at the very end of the readMessage -> eliminate space between items
                        readMessage = readMessage.substring(0,readMessage.length()-1);
                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                        //mConversationArrayAdapter.add(readMessage);
                    }

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,"mNewDevicesArrayAdapter" + mNewDevicesArrayAdapter.toString());
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(!h_adapter.contains(device.getAddress())){
                        h_adapter.add(device.getAddress());
                        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                getActivity().setTitle(R.string.select_device);
                scanButton.setEnabled(true);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

    private void sendMessage(String SSID, String PWD, String UUIDd) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        String uniqueID = java.util.UUID.randomUUID().toString().replace("-","");
        // Check that there's actually something to send
        if (SSID.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            JSONObject mJson = new JSONObject();
            //JSONObject mJson2 = new JSONObject();
            //JSONObject mJson3 = new JSONObject();
            try {
                mJson.put("SSID",SSID);
                mJson.put("PWD",PWD);
                mJson.put("UUID",UUIDd);
                mJson.put("STREAMID",uniqueID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] send = mJson.toString().getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    public boolean isJson(String str) {
        try {
            new JSONObject(str);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public void handleCallback(String str){
        String result;
        String ip;
        if(isCountdown){
            mHandler.removeCallbacks(watchDogTimeOut);
            isCountdown = false;
        }

        //enable user interaction
        //mProgressDialog.dismiss();
        try{
            JSONObject mJSON = new JSONObject(str);
            result = mJSON.getString("result") == null? "" : mJSON.getString("result");
            ip = mJSON.getString("IP") == null? "" : mJSON.getString("IP");
            //Toast.makeText(getActivity(), "result: "+result+", IP: "+ip, Toast.LENGTH_LONG).show();

            if(!result.equals("SUCCESS")){
                Toast.makeText(getActivity(), R.string.config_fail,
                        Toast.LENGTH_LONG).show();
            }else{
//                Toast.makeText(getActivity(), R.string.config_success,
//                            Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),getString(R.string.config_success) + ip,Toast.LENGTH_LONG).show();

            }

        }catch (JSONException e){
            // error handling
            Toast.makeText(getActivity(), "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();
        }

    }

    private final Runnable watchDogTimeOut = new Runnable() {
        @Override
        public void run() {
            isCountdown = false;
            //time out
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(),"No response from RPi",Toast.LENGTH_LONG).show();
            }
        }
    };
}
