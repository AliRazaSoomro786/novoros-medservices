package com.novoros.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.novoros.common.FirebaseHelper;
import com.novoros.common.KEYS;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AddPatientActivity extends AppCompatActivity {
    private final static String TAG = AddPatientActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_patient_activity);

        findViewById(R.id.actionBackpress).setOnClickListener(v -> finish());

        EditText texture_name = findViewById(R.id.texture_name);
        EditText texture_description = findViewById(R.id.texture_description);

        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);

        String selectedDate = "";

        findViewById(R.id.actionAddPatientDetails).setOnClickListener(v -> {
            String name = texture_name.getText().toString();
            String description = texture_description.getText().toString();

            String sTime = startTime.getText().toString();
            String eTime = endTime.getText().toString();

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

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(KEYS.name.toString(), name);
            hashMap.put(KEYS.description.toString(), description);
            hashMap.put(KEYS.date.toString(), selectedDate);
            hashMap.put(KEYS.time.toString(), sTime + " - " + eTime);

            FirebaseHelper.insert(hashMap, new FirebaseHelper.IFirebaseListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddPatientActivity.this, "Patient Added Successfully.", Toast.LENGTH_SHORT).show();

                    Executors.newSingleThreadScheduledExecutor().schedule(() -> finish(), 1, TimeUnit.SECONDS);
                }

                @Override
                public void onFirebaseError(String error) {
                    Log.d(TAG, "onFirebaseError: " + error);
                }
            });

        });


        findViewById(R.id.actionAddPatientDetails).setOnClickListener(v -> {

        });
    }
}
