package com.novoros.ars;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.novoros.common.Schedule;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private final List<Schedule> schedules;
    private final ISchedule listener;
    private final String TAG = ScheduleAdapter.class.getName();
    public boolean checked = false;

    public ScheduleAdapter(List<Schedule> list, ISchedule listener) {
        this.schedules = list;
        this.listener = listener;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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
            Schedule schedule = schedules.get(position);

            int pos = position + 1;
            holder.mPosition.setText(pos + " . ");

//            if (position % 2 == 0)
//                holder.itemView.setBackgroundResource(R.drawable.patient_item_bg_dark);

            String endTime = schedule.getTime().split("-")[1].replace(" ", "");
            String currentTime = DateTime.now().getHourOfDay() + ":" + DateTime.now().getMinuteOfHour();

            if (cheTiming(currentTime, endTime))
                Log.d(TAG, "Schedule time");
            else
                holder.itemView.setBackgroundResource(R.drawable.patient_item_bg_expired);


            holder.itemView.setOnClickListener(v -> {
                if (checked || !cheTiming(currentTime, endTime)) return;
                if (holder.normalGroup.getVisibility() == View.VISIBLE) {
                    holder.normalGroup.setVisibility(View.GONE);
                    holder.expandGroup.setVisibility(View.VISIBLE);
                } else {
                    holder.normalGroup.setVisibility(View.VISIBLE);
                    holder.expandGroup.setVisibility(View.GONE);
                }

                notifyDataSetChanged();

            });

            if (checked || !cheTiming(currentTime, endTime)) {
                holder.expandGroup.setVisibility(View.GONE);
                holder.normalGroup.setVisibility(View.VISIBLE);
            }

            holder.name.setText(schedule.getName());
            holder.time.setText(schedule.getTime());
            holder.expandCheckbox.setOnClickListener(v1 -> listener.onItemClick(schedule));
            holder.expandName.setText(schedule.getName());
            holder.expandDescription.setText(schedule.getDescription());
            holder.expandTime.setText(schedule.getTime());


        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }
    }

    private boolean cheTiming(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            return date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
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

        private final TextView expandName, expandTime, expandDescription;
        private final CheckBox expandCheckbox;

        private final TextView mPosition;

        private Group normalGroup, expandGroup;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.patient_name);
            time = view.findViewById(R.id.preview_time);

            mPosition = view.findViewById(R.id.preview_position);

            expandName = view.findViewById(R.id.patient_name_expand);
            expandTime = view.findViewById(R.id.preview_time_expand);
            expandDescription = view.findViewById(R.id.patient_description_expand);
            expandCheckbox = view.findViewById(R.id.checkBox_expand);

            normalGroup = view.findViewById(R.id.normal_view);
            expandGroup = view.findViewById(R.id.expand_view);
        }
    }
}
