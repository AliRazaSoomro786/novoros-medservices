package com.novoros.ars;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novoros.common.FirebaseHelper;
import com.novoros.common.KEYS;
import com.novoros.common.Schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimeTickReceiver.ITimerTickReceiver {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final List<Schedule> schedules = new ArrayList<>();
    private final TimeTickReceiver mReceiver = new TimeTickReceiver(this);
    private ScheduleAdapter mAdapter;
    private ProgressBar mPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RecyclerView schedule_recyclerView = findViewById(R.id.schedule_recyclerView);
        schedule_recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new ScheduleAdapter(schedules, schedule -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(KEYS.checked.toString(), true);

            FirebaseHelper.update(hashMap, schedule.getKey(), new FirebaseHelper.IFirebaseListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "Patient Marked as checked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFirebaseError(String error) {
                    Log.d(TAG, "onFirebaseError: " + error);
                }
            });
        });

        schedule_recyclerView.setAdapter(mAdapter);

        mPb = findViewById(R.id.progressBar);

        TextView preview_date_year = findViewById(R.id.preview_date_year);
        TextView preview_date_year_day = findViewById(R.id.preview_date_year_day);

        DateTime dateTime = DateTime.now();

        String month = dateTime.monthOfYear().getAsShortText();
        String day = dateTime.dayOfWeek().getAsText();

        int date = dateTime.getDayOfMonth();
        int year = dateTime.getYear();

        preview_date_year.setText(month + " " + year);
        preview_date_year_day.setText(date + " " + month + "-" + day);

        Group menu_group = findViewById(R.id.menu_group);

        findViewById(R.id.action_more).setOnClickListener(v -> {
            if (menu_group.getVisibility() == View.VISIBLE)
                menu_group.setVisibility(View.GONE);
            else menu_group.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.menue_item_checked_patient).setOnClickListener(v -> {
            menu_group.setVisibility(View.GONE);
            loadSchedules(true);
        });

        findViewById(R.id.menue_item_new_patient).setOnClickListener(v -> {
            menu_group.setVisibility(View.GONE);
            loadSchedules(false);
        });

        findViewById(R.id.view).setOnClickListener(v -> menu_group.setVisibility(View.GONE));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void loadSchedules(boolean checked) {
        mPb.setVisibility(View.VISIBLE);

        FirebaseHelper.getScheduleUpdates(new FirebaseHelper.ISchedules() {
            @Override
            public void onSchedules(List<Schedule> newSchedules) {
                mPb.setVisibility(View.GONE);
                mAdapter.setChecked(checked);
                schedules.clear();

                for (Schedule schedule : newSchedules) {
                    if (isValid(schedule.getDate())) schedules.add(schedule);
                }

                filterList();

                mAdapter.notifyDataSetChanged();

                Log.d(TAG, "onSchedules: " + newSchedules.size());

            }


            @Override
            public void onFirebaseError(String error) {
                mPb.setVisibility(View.GONE);
                Log.d(TAG, "onFirebaseError: " + error);
            }
        }, checked);
    }

    private void filterList() {
        Collections.sort(schedules, (o1, o2) -> {
            try {
                return new SimpleDateFormat("hh:mm").parse(o1.getTime().split("-")[0]).compareTo(new SimpleDateFormat("hh:mm").parse(o2.getTime().split("-")[0]));
            } catch (java.text.ParseException e) {
                return 0;
            }
        });

    }

    private boolean isValid(String date) {
        DateTime dateTime = DateTime.now();
        String currentDate = dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth();

        int result = DateTimeComparator.getDateOnlyInstance().compare(currentDate, date);
        return result == 0;
    }

    @Override
    public void onTimeChange() {
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }
}