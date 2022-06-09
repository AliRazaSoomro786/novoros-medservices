package com.novoros.admin.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novoros.admin.R;
import com.novoros.common.Schedule;

import java.util.List;


public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {
    public List<Schedule> schedules;
    private final IPatientAdapter listener;

    private final boolean clickable;

    private final String TAG = PatientAdapter.class.getName();

    public PatientAdapter(List<Schedule> list, boolean clickable, IPatientAdapter listener) {
        this.schedules = list;
        this.listener = listener;
        this.clickable = clickable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_patient_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Schedule schedule = schedules.get(position);
            holder.name.setText(schedule.getName());
            holder.time.setText(schedule.getTime());
            holder.description.setText(schedule.getDescription());

            holder.itemView.setOnClickListener(v -> {
                if (clickable) listener.onItemClick(schedule);
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public interface IPatientAdapter {
        void onItemClick(Schedule schedule);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, time, description;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.patient_name);
            time = view.findViewById(R.id.preview_time);
            description = view.findViewById(R.id.patient_description);
        }
    }
}
