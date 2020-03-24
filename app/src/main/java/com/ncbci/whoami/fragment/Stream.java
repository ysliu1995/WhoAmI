package com.ncbci.whoami.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncbci.whoami.R;

import tcking.github.com.giraffeplayer2.VideoInfo;
import tcking.github.com.giraffeplayer2.VideoView;

public class Stream extends Fragment {
    private final static String TAG = "Stream";
    private VideoView videoView;
    private View v;
    private FirebaseAuth mAuth;
    private String stream_id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, null);
        v = view;
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = v.findViewById(R.id.video_view);


    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
            Log.d("test", mAuth.getUid()+"");
            mdatabase.child("Users").child(mAuth.getUid()).child("streamURL").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    stream_id = dataSnapshot.getValue().toString();
                    Log.d("test", dataSnapshot.getValue() + "");
                    Log.d("test", stream_id);
                    String stream_url = "rtmp://140.116.245.37/rtmp/" + stream_id;
                    videoView.setVideoPath(stream_url).getPlayer().start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
