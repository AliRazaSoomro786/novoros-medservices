package com.novoros.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.novoros.common.FirebaseHelper;
import com.novoros.common.Global;
import com.novoros.common.KEYS;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EditPatientActivity extends AppCompatActivity {
    private final static String TAG = EditPatientActivity.class.getSimpleName();
    private String selectedDate = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_patient_activity);

        findViewById(R.id.actionBackpress).setOnClickListener(v -> finish());

        EditText texture_name = findViewById(R.id.texture_name);
        EditText texture_description = findViewById(R.id.texture_description);

        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);

        startTime.setOnClickListener(v -> timePicker(startTime));
        endTime.setOnClickListener(v -> timePicker(endTime));

        texture_name.setText(Global.schedule.getName());
        texture_description.setText(Global.schedule.getDescription());

        startTime.setText(Global.split(0, Global.schedule.getTime()));
        endTime.setText(Global.split(0, Global.schedule.getTime()));

        selectedDate = Global.schedule.getDate();

        findViewById(R.id.actionDeletePatient).setOnClickListener(v -> {
            delete();
        });

        findViewById(R.id.actionAddPatientDetails).setOnClickListener(v -> {
            String name = texture_name.getText().toString();
            String description = texture_description.getText().toString();

            String sTime = startTime.getText().toString().replace(" ", "");
            String eTime = endTime.getText().toString().replace(" ", "");

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter Patient Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sTime.isEmpty() || eTime.isEmpty()) {
                Toast.makeText(this, "Enter Schedule time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Enter Patient details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
                return;
            }

            DateTime dateTime = DateTime.now();

            int sHour = Integer.parseInt(sTime.split(":")[0]);
            int sMinute = Integer.parseInt(sTime.split(":")[1]);

            int eHour = Integer.parseInt(eTime.split(":")[0]);
            int eMinute = Integer.parseInt(eTime.split(":")[1]);

            if (dateTime.withHourOfDay(sHour).withMinuteOfHour(sMinute).getMillis() >
                    dateTime.withHourOfDay(eHour).withMinuteOfHour(eMinute).getMillis()) {
                Toast.makeText(this, "Please select valid time", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(KEYS.name.toString(), name);
            hashMap.put(KEYS.description.toString(), description);
            hashMap.put(KEYS.date.toString(), selectedDate);
            hashMap.put(KEYS.time.toString(), sTime + " - " + eTime);
            hashMap.put(KEYS.checked.toString(), false);

            FirebaseHelper.update(hashMap, Global.schedule.getKey(), new FirebaseHelper.IFirebaseListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(EditPatientActivity.this, "Patient Added Successfully.", Toast.LENGTH_SHORT).show();

                    Executors.newSingleThreadScheduledExecutor().schedule(() -> finish(), 1, TimeUnit.SECONDS);
                }

                @Override
                public void onFirebaseError(String error) {
                    Log.d(TAG, "onFirebaseError: " + error);
                }
            });

        });

        findViewById(R.id.actionDatePicker).setOnClickListener(v -> {
            datePicker();
        });
    }

    private void datePicker() {
        LayoutInflater factory = LayoutInflater.from(this);

        final View mView = factory.inflate(R.layout.custom_date_picker, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        DatePicker datePicker = mView.findViewById(R.id.datePicker);

        datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            int month = monthOfYear + 1;
            selectedDate = dayOfMonth + "-" + month + "-" + year;

            dialog.dismiss();
        });

        dialog.setView(mView);
        dialog.setCancelable(false);

        dialog.show();
    }

    private void timePicker(TextView textView) {
        LayoutInflater factory = LayoutInflater.from(this);

        final View mView = factory.inflate(R.layout.time_picker, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        TimePicker timePicker = mView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            if (minute == 0) {
                textView.setText(hourOfDay + ":" + 00);
                return;
            }
            textView.setText(hourOfDay + ":" + minute);
        });

        mView.findViewById(R.id.actionNext).setOnClickListener(v -> dialog.dismiss());

        dialog.setView(mView);
        dialog.setCancelable(false);

        dialog.show();
    }

    private void delete() {
        LayoutInflater factory = LayoutInflater.from(this);

        final View mView = factory.inflate(R.layout.schedule_delete_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        mView.findViewById(R.id.actionDeletePatientDialog).setOnClickListener(v -> {
            FirebaseHelper.delete(Global.schedule.getKey(), new FirebaseHelper.IFirebaseListener() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    Toast.makeText(EditPatientActivity.this, "Record deleted", Toast.LENGTH_SHORT).show();
                    Executors.newSingleThreadScheduledExecutor().schedule(() -> finish(), 1, TimeUnit.SECONDS);
                }

                @Override
                public void onFirebaseError(String error) {
                    dialog.dismiss();
                    Log.d(TAG, "onFirebaseError: " + error);
                }
            });

        });
        mView.findViewById(R.id.actionCancleDelete).setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.setView(mView);
        dialog.setCancelable(false);

        dialog.show();
    }

}
