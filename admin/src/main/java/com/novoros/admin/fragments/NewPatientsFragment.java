package com.novoros.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novoros.admin.EditPatientActivity;
import com.novoros.admin.R;
import com.novoros.admin.adapters.PatientAdapter;
import com.novoros.common.FirebaseHelper;
import com.novoros.common.Global;
import com.novoros.common.Schedule;

import java.util.ArrayList;
import java.util.List;

public class NewPatientsFragment extends Fragment {
    private final static String TAG = NewPatientsFragment.class.getSimpleName();
    private final List<Schedule> schedules = new ArrayList<>();
    private PatientAdapter mAdapter;
    private ProgressBar mPb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.patient_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        mAdapter = new PatientAdapter(schedules, true, schedule -> {
            Global.schedule = schedule;
            startActivity(new Intent(inflater.getContext(), EditPatientActivity.class));
        });

        recyclerView.setAdapter(mAdapter);

        mPb = view.findViewById(R.id.progressBar);

        return view;
    }

    public void search(String string) {
        filter(string);
    }

    private void filter(CharSequence charSequence) {
        List<Schedule> mFilteredList;

        String charString = charSequence.toString().toLowerCase();
        if (charString.isEmpty()) {

            mFilteredList = this.schedules;
        } else {

            List<Schedule> filteredList = new ArrayList<>();

            for (Schedule schedule : schedules) {

                if (schedule.getName().toLowerCase().contains(charString)
                        || schedule.getDescription().toLowerCase().contains(charString)
                        || schedule.getTime().toLowerCase().contains(charString)) {

                    filteredList.add(schedule);
                }
            }

            mFilteredList = filteredList;
        }

        mAdapter.schedules = mFilteredList;
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
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
