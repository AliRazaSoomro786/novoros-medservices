package com.novoros.ars;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novoros.common.FirebaseHelper;
import com.novoros.common.Schedule;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final List<Schedule> schedules = new ArrayList<>();

    private ScheduleAdapter mAdapter;
    private ProgressBar mPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView schedule_recyclerView = findViewById(R.id.schedule_recyclerView);
        schedule_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ScheduleAdapter(schedules, schedule -> {

        });

        schedule_recyclerView.setAdapter(mAdapter);

        mPb = findViewById(R.id.progressBar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }

    private void loadSchedules() {
        mPb.setVisibility(View.VISIBLE);

        FirebaseHelper.getScheduleUpdates(new FirebaseHelper.ISchedules() {
            @Override
            public void onSchedules(List<Schedule> newSchedules) {
                mPb.setVisibility(View.GONE);

                schedules.clear();
                schedules.addAll(newSchedules);
                mAdapter.notifyDataSetChanged();

                Log.d(TAG, "onSchedules: " + newSchedules.size());

            }


            @Override
            public void onFirebaseError(String error) {
                mPb.setVisibility(View.GONE);
                Log.d(TAG, "onFirebaseError: " + error);
            }
        }, false);
    }

}