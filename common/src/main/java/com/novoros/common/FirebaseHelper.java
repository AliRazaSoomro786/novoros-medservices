package com.novoros.common;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FirebaseHelper {
    private final static String TAG = FirebaseHelper.class.getSimpleName();

    public static String getValue(String child, DataSnapshot snapshot) {
        if (snapshot.hasChild(child)) return snapshot.child(child).getValue().toString();
        else return "";
    }

    public static void getSchedules(ISchedules listener, boolean isChecked) {
        List<Schedule> schedules = new ArrayList<>();

        reference().child(KEYS.schedules.toString()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) listener.onSchedules(schedules);
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Boolean checked = (Boolean) snap.child(KEYS.checked.toString()).getValue();
                            if (checked == null) checked = false;

                            if (checked != isChecked) continue;

                            String date = getValue(KEYS.date.toString(), snap);
                            String description = getValue(KEYS.description.toString(), snap);

                            String name = getValue(KEYS.name.toString(), snap);
                            String time = getValue(KEYS.time.toString(), snap);


                            schedules.add(new Schedule(snap.getKey(), checked, date, description, name, time));
                        }

                        listener.onSchedules(schedules);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "onCancelled: ", error.toException());
                        listener.onFirebaseError(error.getMessage());
                    }
                });

    }

    public static void getScheduleUpdates(ISchedules listener, boolean isChecked) {
        List<Schedule> schedules = new ArrayList<>();

        reference().child(KEYS.schedules.toString()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        schedules.clear();

                        if (snapshot.getValue() == null) listener.onSchedules(schedules);

                        for (DataSnapshot snap : snapshot.getChildren()) {

                            Boolean checked = (Boolean) snap.child(KEYS.checked.toString()).getValue();
                            if (checked == null) checked = false;
                            if (checked) continue;

                            String date = getValue(KEYS.date.toString(), snap);
                            String description = getValue(KEYS.description.toString(), snap);

                            String name = getValue(KEYS.name.toString(), snap);
                            String time = getValue(KEYS.time.toString(), snap);


                            schedules.add(new Schedule(snap.getKey(), checked, date, description, name, time));
                        }

                        listener.onSchedules(schedules);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "onCancelled: ", error.toException());
                        listener.onFirebaseError(error.getMessage());
                    }
                });

    }

    public static void insert(HashMap<String, Object> hashMap, IFirebaseListener iInsert) {

        reference().child(KEYS.schedules.toString()).push().setValue(hashMap)
                .addOnFailureListener(e -> {
                    iInsert.onFirebaseError(e.getMessage());

                }).addOnCompleteListener(task -> {
                    iInsert.onSuccess();
                });
    }

    public static void update(HashMap<String, Object> hashMap, String key, IFirebaseListener iFirebaseListener) {

        reference().child(KEYS.schedules.toString()).child(key).updateChildren(hashMap)
                .addOnFailureListener(e -> {
                    iFirebaseListener.onFirebaseError(e.getMessage());
                }).addOnCompleteListener(task -> {
                    iFirebaseListener.onSuccess();
                });
    }

    public static void delete(String key, IFirebaseListener listener) {
        reference().child(KEYS.schedules.toString()).child(key).removeValue()
                .addOnCompleteListener(task -> {
                    listener.onSuccess();
                }).addOnFailureListener(e -> {
                    listener.onFirebaseError(e.getMessage());
                });
    }

    public static DatabaseReference reference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public interface ISchedules extends IFirebaseError {
        void onSchedules(List<Schedule> schedules);
    }

    public interface IFirebaseListener extends IFirebaseError {
        void onSuccess();
    }

    public interface IFirebaseError {
        void onFirebaseError(String error);
    }
}
