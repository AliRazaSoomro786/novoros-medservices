package com.novoros.ars;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novoros.common.Schedule;

import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private final List<Schedule> schedules;
    private final ISchedule listener;

    private final String TAG = ScheduleAdapter.class.getName();

    public ScheduleAdapter(List<Schedule> list, ISchedule listener) {
        this.schedules = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (position % 2 == 0)
                holder.itemView.setBackgroundResource(R.drawable.patient_item_bg_dark);

            Schedule schedule = schedules.get(position);
            holder.name.setText(schedule.getName());
            holder.time.setText(schedule.getTime());

            holder.itemView.setOnClickListener(v -> {
                listener.onItemClick(schedule);
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public interface ISchedule {
        void onItemClick(Schedule schedule);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, time;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.patient_name);
            time = view.findViewById(R.id.preview_time);
        }
    }
}
