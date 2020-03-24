package com.ncbci.whoami.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncbci.whoami.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends Fragment {
    static final String TAG = "Home";
    private View v;
    private FirebaseAuth mAuth;
    private LineChart CO2Chart, PMChart;
    private DonutProgress tempChart, humChart;
    private LineDataSet CO2dataSet, PM1Set, PM2dot5Set, PM10Set;
    private LineData CO2data;
    private ArrayList<ILineDataSet> PMdataSets;
    private int DataIndex = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private Date date;
    private String filename;
    private Button record;
    private Boolean issave = false;
    private String text;
    private TextView temperature, humidity;
    private int windowSize = 30;

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
//        initPMChart();
        initCO2Chart();
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
        mAuth = FirebaseAuth.getInstance();
        GetRealTimeData();
    }

    private void initView(){
        CO2Chart = v.findViewById(R.id.CO2Chart);
        PMChart = v.findViewById(R.id.PMChart);
        tempChart = v.findViewById(R.id.tempChart);
        temperature = v.findViewById(R.id.temperature);
        humidity = v.findViewById(R.id.humidity);

//        humChart = v.findViewById(R.id.humChart);
//        record = v.findViewById(R.id.record_data);
//        record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                date = new Date();
//                filename = dateFormat.format(date);
//                issave = true;
//                Toast.makeText(getActivity(),"Save to : " + filename, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void initPMChart(){
        ArrayList<Entry> PM1Entries = new ArrayList<>();
        ArrayList<Entry> PM2dot5Entries = new ArrayList<>();
        ArrayList<Entry> PM10Entries = new ArrayList<>();

        PM1Entries.add(new Entry(DataIndex, 0 ));
        PM2dot5Entries.add(new Entry(DataIndex,10));
        PM10Entries.add(new Entry(DataIndex, 20));

        PMdataSets = new ArrayList<>(); // for adding multiple plots

        PM1Set = new LineDataSet(PM1Entries,"PM1");
        PM2dot5Set = new LineDataSet(PM2dot5Entries,"PM2.5");
        PM10Set = new LineDataSet(PM10Entries,"PM10");
        PM1Set.setDrawValues(false);
        PM2dot5Set.setDrawValues(false);
        PM10Set.setDrawValues(false);

        PM1Set.setDrawCircles(false);
        PM2dot5Set.setDrawCircles(false);
        PM10Set.setDrawCircles(false);

        PM1Set.setColor(Color.GREEN);
        PM1Set.setCircleColor(Color.GREEN);
        PM2dot5Set.setColor(Color.BLUE);
        PM2dot5Set.setCircleColor(Color.BLUE);
        PM10Set.setColor(Color.RED);
        PM10Set.setCircleColor(Color.RED);

        PMdataSets.add(PM1Set);
        PMdataSets.add(PM2dot5Set);
        PMdataSets.add(PM10Set);

        PMChart.setData(new LineData(PMdataSets));
        PMChart.invalidate();

        PMChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        PMChart.getAxisRight().setEnabled(false);
        PMChart.getDescription().setEnabled(false);
    }

    private void initCO2Chart(){
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(DataIndex, 5));

        CO2dataSet = new LineDataSet(entries, "CO2");
        CO2dataSet.setColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
        CO2dataSet.setFillColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
        CO2dataSet.setValueTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
        CO2dataSet.setFillAlpha(150);
        CO2dataSet.setDrawFilled(true);
        CO2dataSet.setDrawValues(false);
        CO2dataSet.setLineWidth(2f);
        CO2dataSet.setDrawCircles(false);
        CO2dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        CO2Chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        CO2Chart.getXAxis().setDrawLabels(false);
        CO2Chart.getAxisRight().setEnabled(false);
        CO2Chart.getDescription().setEnabled(false);
        CO2Chart.getLegend().setEnabled(false);
        CO2Chart.getAxisLeft().setDrawGridLines(false);
        CO2Chart.getAxisRight().setDrawGridLines(false);
        CO2Chart.getXAxis().setDrawGridLines(false);

        CO2data = new LineData(CO2dataSet);
        CO2Chart.setData(CO2data);
        CO2Chart.invalidate();

    }

    private void GetRealTimeData(){
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("Users").child(mAuth.getUid()).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.getValue()+"");
                /**
                 *  write the record data to txt
                 */
                date = new Date();
                String data_str = dateFormat.format(date);
                text = data_str + "   :" +dataSnapshot.child("PM1").getValue().toString()+","+dataSnapshot.child("PM2dot5").getValue().toString()+","+dataSnapshot.child("PM10").getValue().toString()+","+dataSnapshot.child("CO2").getValue().toString()+","+dataSnapshot.child("temperature").getValue().toString()+","+dataSnapshot.child("humidity").getValue().toString()+"\n";
                // TODO write file
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(issave){
                            WriteFileExample(filename,text);
                        }
                    }
                }).start();

                refreshData(Float.parseFloat(dataSnapshot.child("PM1").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("PM2dot5").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("PM10").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("CO2").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("temperature").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("humidity").getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(float PM1, float PM2dot5, float PM10, float CO2, int temperature, int humidity){
        DataIndex++;

//        tempChart.setProgress(temperature);
//        humChart.setProgress(humidity);
        this.temperature.setText(temperature + " °C");
        this.humidity.setText(humidity + " %");
        if(CO2dataSet.getEntryCount() > windowSize) CO2dataSet.removeFirst();
        CO2dataSet.addEntry(new Entry(DataIndex, CO2));
        CO2Chart.getLineData().notifyDataChanged();
        CO2Chart.notifyDataSetChanged();
        CO2Chart.invalidate();
//        if(PM1Set.getEntryCount() > windowSize) PM1Set.removeFirst();
//        if(PM2dot5Set.getEntryCount() > windowSize) PM2dot5Set.removeFirst();
//        if(PM10Set.getEntryCount() > windowSize) PM10Set.removeFirst();
//        PM1Set.addEntry(new Entry(DataIndex, PM1));
//        PM2dot5Set.addEntry(new Entry(DataIndex, PM2dot5));
//        PM10Set.addEntry(new Entry(DataIndex, PM10));
//        PMChart.getLineData().notifyDataChanged();
//        PMChart.notifyDataSetChanged();
//        PMChart.invalidate();
    }

    private void WriteFileExample(String Filename,String message) {
        FileOutputStream fop = null;
        File file;
        String content = message;

        try {
            File sdcard = Environment.getExternalStorageDirectory();
            file = new File(sdcard,"Log-"+Filename+".txt");
            Log.i("Write File:", file + "");
            fop = new FileOutputStream(file, true);

            if (!file.exists()) {
                file.createNewFile();
            }

            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
            //Looper.prepare();
            //Toast.makeText(MainActivity.this, "Save Success, Path:" + file, Toast.LENGTH_SHORT).show();
            //Looper.loop();

        } catch (IOException e) {
            Log.i("Write E:", e + "");
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                Log.i("Write IOException", e + "");
                e.printStackTrace();
            }
        }
    }

}
