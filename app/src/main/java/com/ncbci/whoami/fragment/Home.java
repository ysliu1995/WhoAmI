package com.ncbci.whoami.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncbci.whoami.Activity.LoginActivity;
import com.ncbci.whoami.Activity.MainActivity;
import com.ncbci.whoami.CustomBarChartRender;
import com.ncbci.whoami.R;
import com.ncbci.whoami.dialog.ClassRoomDialog;
import com.ncbci.whoami.dialog.ProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

public class Home extends Fragment {
    static final String TAG = "Home";
    private View v;
    private FirebaseAuth mAuth;
    private LineChart Chart;
    private DonutProgress tempChart;
    private LineDataSet dataSet;
    private int DataIndex = 0, windowSize = 30;
    private TextView temperature, humidity, CO2Label, PMLabel;
    private Switch chartSwitch;
    private ConstraintLayout homeTopLayout;
    private TextView level;
    private GifImageView airStatus, speak;
    private BarChart physicalActivityChart;
    private BarDataSet bardataset;
    private CardView tempCard, humCard, CO2Card, PMCard;

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
        setListener();
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
        level = v.findViewById(R.id.level);
        airStatus = v.findViewById(R.id.air_status);
        speak = v.findViewById(R.id.speak);
        physicalActivityChart = v.findViewById(R.id.physicalActivityChart);
        tempCard = v.findViewById(R.id.tempCard);
        humCard = v.findViewById(R.id.humCard);
        CO2Card = v.findViewById(R.id.CO2Card);
        PMCard = v.findViewById(R.id.PMCard);

        mAuth = FirebaseAuth.getInstance();
    }

    private void setListener() {
        chartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Chart.setVisibility(View.INVISIBLE);
                    physicalActivityChart.setVisibility(View.VISIBLE);
                    physicalActivityChart.animateY(1000);
                } else {
                    Chart.setVisibility(View.VISIBLE);
                    physicalActivityChart.setVisibility(View.INVISIBLE);
                }

            }
        });
        airStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak.setImageResource(R.drawable.cleanv2);
                airStatus.setImageResource(R.drawable.air_clean_on);
                DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                mdatabase.child("Users").child(mAuth.getUid()).child("airStatus").setValue(1);
                mdatabase.child("Users").child(mAuth.getUid()).child("airStatus").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue().toString().equals("1")){

                        } else{
                            speak.setImageResource(R.drawable.diagv2);
                            airStatus.setImageResource(R.drawable.air_clean_off);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        tempCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClassRoomDialog(v.getContext()).show();
            }
        });
        humCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClassRoomDialog(getContext()).show();
            }
        });
        CO2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClassRoomDialog(getContext()).show();
            }
        });
        PMCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClassRoomDialog(getContext()).show();
            }
        });
    }

    private void changeLevelColor(int level){
        int color1, color2;

        switch (level){
            case 1:
                color1 = R.color.theme4Primary;
                color2 = R.color.theme4PrimaryDark;
                this.level.setText("良好");
                break;
            case 2:
                color1 = R.color.theme2Primary;
                color2 = R.color.theme2PrimaryDark;
                this.level.setText("普通");
                break;
            case 3:
                color1 = R.color.theme1Primary;
                color2 = R.color.theme1PrimaryDark;
                this.level.setText("危險");
                break;
            default:
                color1 = R.color.theme4Primary;
                color2 = R.color.theme4PrimaryDark;
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
        bardataset.setColor(ContextCompat.getColor(v.getContext(), color1));
//        bardataset.setValueTextColor(ContextCompat.getColor(v.getContext(), color2));
    }

    private void initChart(){
        //initial PM2.5 chart
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(DataIndex, 15));

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

        LineData data1 = new LineData(dataSet);
        Chart.setData(data1);
        Chart.invalidate();

        //initial Physical Activity chart
        int[] fakeData = {15, 17, 16, 14, 12, 18, 16, 5, 7, 3, 8, 2, 4, 9, 6, 11, 10, 4, 0, 0, 0, 0, 0, 0};
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i=0;i<24;i++) {
            barEntries.add(new BarEntry(i, fakeData[i]));
        }


        physicalActivityChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        physicalActivityChart.getAxisRight().setEnabled(false);
        physicalActivityChart.getDescription().setEnabled(false);
        physicalActivityChart.getLegend().setEnabled(false);
        physicalActivityChart.getAxisLeft().setDrawGridLines(false);
        physicalActivityChart.getAxisLeft().setDrawLabels(false);
        physicalActivityChart.getAxisRight().setDrawGridLines(false);
        physicalActivityChart.getXAxis().setDrawGridLines(false);
        physicalActivityChart.getXAxis().setLabelCount(24);
        physicalActivityChart.getAxisLeft().setAxisMinimum(0);

        bardataset = new BarDataSet(barEntries, "Physical Activity");
        bardataset.setColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimary));
        BarData data = new BarData(bardataset);
        data.setDrawValues(false);
        physicalActivityChart.setData(data);
        physicalActivityChart.animateY(1000);

        CustomBarChartRender barChartRender = new CustomBarChartRender(physicalActivityChart,physicalActivityChart.getAnimator(),physicalActivityChart.getViewPortHandler());
        barChartRender.setRadius(10);
        physicalActivityChart.setRenderer(barChartRender);

    }

    private void GetRealTimeData(){
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("Users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                changeLevelColor(Integer.parseInt(dataSnapshot.child("level").getValue().toString()));
                changeAirStatus(Integer.parseInt(dataSnapshot.child("airStatus").getValue().toString()));
                refreshData(
                        Float.parseFloat(dataSnapshot.child("data").child("PM2dot5").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("data").child("CO2").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("data2").child("temperature").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("data2").child("humidity").getValue().toString())
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(float PM2dot5, float CO2, int temperature, int humidity){
        DataIndex++;
        this.temperature.setText(temperature+"");
        this.humidity.setText(humidity+"");
        this.CO2Label.setText((int)CO2+"");
        this.PMLabel.setText((int)PM2dot5+"");
        if(dataSet.getEntryCount() > windowSize) dataSet.removeFirst();
        dataSet.addEntry(new Entry(DataIndex, PM2dot5));
        Chart.getLineData().notifyDataChanged();
        Chart.notifyDataSetChanged();
        Chart.invalidate();
    }

    private void changeAirStatus(int status){
        Log.d(TAG, status + "");
        if(status == 1) {
            airStatus.setImageResource(R.drawable.air_clean_on);
            speak.setImageResource(R.drawable.cleanv2);
        } else {
            airStatus.setImageResource(R.drawable.air_clean_off);
            speak.setImageResource(R.drawable.diagv2);
        }
    }
}
