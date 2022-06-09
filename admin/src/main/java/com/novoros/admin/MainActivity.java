package com.novoros.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.novoros.admin.fragments.CheckedPatientFragment;
import com.novoros.admin.fragments.NewPatientsFragment;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private int selectedFragment = 0;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedFragment = tab.getPosition();
                showFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected: " + tab.getPosition());

            }
        });

        EditText texture_search = findViewById(R.id.texture_search);

        texture_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentFragment instanceof NewPatientsFragment)
                    ((NewPatientsFragment) currentFragment).search(s.toString());
                else if (currentFragment instanceof CheckedPatientFragment)
                    ((CheckedPatientFragment) currentFragment).search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Group search_group = findViewById(R.id.search_group);

        findViewById(R.id.actionSearch).setOnClickListener(v -> {
            search_group.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.actionBackPressSearch).setOnClickListener(v -> {
            search_group.setVisibility(View.GONE);
        });

        findViewById(R.id.actionAddPatient).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddPatientActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFragment(selectedFragment);
    }

    private void showFragment(int position) {
        currentFragment = position == 0 ? new NewPatientsFragment() : new CheckedPatientFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment).commit();
    }

    public interface ISearchListener {
        void onTextChange(String text);
    }
}