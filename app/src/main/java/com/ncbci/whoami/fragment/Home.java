package com.ncbci.whoami.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncbci.whoami.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Home extends Fragment {
    static final String TAG = "Home";
    private View v;
    private FirebaseAuth mAuth;
    private LineChart Chart;
    private DonutProgress tempChart;
    private LineDataSet dataSet;
    private LineData data;
    private int DataIndex = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private Date date;
    private Boolean issave = false;
    private String text;
    private TextView temperature, humidity, CO2Label, PMLabel;
    private Switch chartSwitch;
    private int windowSize = 30;
    private Boolean chartFlag = false;
    private ConstraintLayout homeTopLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        v = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initChart();
        // check the write permission----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        } else {
        }
        // check the write permission ----------------------------------------------------------------

        GetRealTimeData();
    }

    private void initView(){
        Chart = v.findViewById(R.id.Chart);
        chartSwitch = v.findViewById(R.id.switch2);
        tempChart = v.findViewById(R.id.tempChart);
        temperature = v.findViewById(R.id.temperature);
        humidity = v.findViewById(R.id.humidity);
        CO2Label = v.findViewById(R.id.CO2Label);
        PMLabel = v.findViewById(R.id.PMLabel);
        homeTopLayout = v.findViewById(R.id.homeTopLayout);

        mAuth = FirebaseAuth.getInstance();

        chartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Chart.clearValues();
                    initChart();
                    chartFlag = true;
                } else {
                    Chart.clearValues();
                    initChart();
                    chartFlag = false;
                }
            }
        });
    }
    private void changeLevelColor(int level){
        int color1, color2;

        switch (level){
            case 1:
                color1 = R.color.theme1Primary;
                color2 = R.color.theme1PrimaryDark;
                break;
            case 2:
                color1 = R.color.theme2Primary;
                color2 = R.color.theme2PrimaryDark;
                break;
            case 3:
                color1 = R.color.theme3Primary;
                color2 = R.color.theme3PrimaryDark;
                break;
            case 4:
                color1 = R.color.theme4Primary;
                color2 = R.color.theme4PrimaryDark;
                break;
            default:
                color1 = R.color.theme1Primary;
                color2 = R.color.theme1PrimaryDark;
                break;
        }
        getActivity().getWindow().setStatusBarColor(getResources().getColor(color2, null));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(color1, null)));
        homeTopLayout.setBackground(new ColorDrawable(getResources().getColor(color1, null)));
        tempChart.setBackground(new ColorDrawable(getResources().getColor(color1, null)));
        tempChart.setFinishedStrokeColor(Color.WHITE);
        tempChart.setFinishedStrokeColor(getResources().getColor(color2, null));
        dataSet.setColor(ContextCompat.getColor(v.getContext(), color1));
        dataSet.setFillColor(ContextCompat.getColor(v.getContext(), color1));
        dataSet.setValueTextColor(ContextCompat.getColor(v.getContext(), color2));
    }

    private void initChart(){
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(DataIndex, 0));

        dataSet = new LineDataSet(entries, "CO2");
        dataSet.setColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
        dataSet.setFillColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
        dataSet.setFillAlpha(150);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Chart.getXAxis().setDrawLabels(false);
        Chart.getAxisRight().setEnabled(false);
        Chart.getDescription().setEnabled(false);
        Chart.getLegend().setEnabled(false);
        Chart.getAxisLeft().setDrawGridLines(false);
        Chart.getAxisRight().setDrawGridLines(false);
        Chart.getXAxis().setDrawGridLines(false);

        data = new LineData(dataSet);
        Chart.setData(data);
        Chart.invalidate();

    }

    private void GetRealTimeData(){
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("Users").child(mAuth.getUid()).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                /**
//                 *  write the record data to txt
//                 */
//                date = new Date();
//                String data_str = dateFormat.format(date);
//                text = data_str + "   :" +dataSnapshot.child("PM1").getValue().toString()+","+dataSnapshot.child("PM2dot5").getValue().toString()+","+dataSnapshot.child("PM10").getValue().toString()+","+dataSnapshot.child("CO2").getValue().toString()+","+dataSnapshot.child("temperature").getValue().toString()+","+dataSnapshot.child("humidity").getValue().toString()+"\n";
//                // TODO write file
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(issave){
//                            WriteFileExample(filename,text);
//                        }
//                    }
//                }).start();
                changeLevelColor(Integer.parseInt(dataSnapshot.child("level").getValue().toString()));

                refreshData(
                        Float.parseFloat(dataSnapshot.child("PM2dot5").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("CO2").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("temperature").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("humidity").getValue().toString())
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(float PM2dot5, float CO2, int temperature, int humidity){
        DataIndex++;

        this.temperature.setText(temperature + " °C");
        this.humidity.setText(humidity + " %");
        this.CO2Label.setText(CO2 + " ppm");
        this.PMLabel.setText(PM2dot5 + " μg/m3");
        if(dataSet.getEntryCount() > windowSize) dataSet.removeFirst();
        if(chartFlag){
            dataSet.addEntry(new Entry(DataIndex, PM2dot5));
        } else {
            dataSet.addEntry(new Entry(DataIndex, CO2));
        }

        Chart.getLineData().notifyDataChanged();
        Chart.notifyDataSetChanged();
        Chart.invalidate();
    }

//    private void WriteFileExample(String Filename,String message) {
//        FileOutputStream fop = null;
//        File file;
//        String content = message;
//
//        try {
//            File sdcard = Environment.getExternalStorageDirectory();
//            file = new File(sdcard,"Log-"+Filename+".txt");
//            Log.i("Write File:", file + "");
//            fop = new FileOutputStream(file, true);
//
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            byte[] contentInBytes = content.getBytes();
//
//            fop.write(contentInBytes);
//            fop.flush();
//            fop.close();
//            //Looper.prepare();
//            //Toast.makeText(MainActivity.this, "Save Success, Path:" + file, Toast.LENGTH_SHORT).show();
//            //Looper.loop();
//
//        } catch (IOException e) {
//            Log.i("Write E:", e + "");
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fop != null) {
//                    fop.close();
//                }
//            } catch (IOException e) {
//                Log.i("Write IOException", e + "");
//                e.printStackTrace();
//            }
//        }
//    }

}
