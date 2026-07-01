package com.dropsett.app.ui;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.dropsett.app.R;
import com.dropsett.app.util.UserPreferences;

public class SettingsActivity extends AppCompatActivity {

    private UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new UserPreferences(this);

        RadioGroup rgEquipment = findViewById(R.id.rgEquipment);

        // set current selection
        switch (prefs.getEquipmentProfile()) {
            case UserPreferences.PROFILE_BODYWEIGHT:
                rgEquipment.check(R.id.rbBodyweight);
                break;
            case UserPreferences.PROFILE_HOME_DUMBBELLS:
                rgEquipment.check(R.id.rbHomeDumbbells);
                break;
            case UserPreferences.PROFILE_GARAGE:
                rgEquipment.check(R.id.rbGarage);
                break;
            case UserPreferences.PROFILE_FULL_GYM:
            default:
                rgEquipment.check(R.id.rbFullGym);
                break;
        }

        rgEquipment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBodyweight) {
                prefs.setEquipmentProfile(UserPreferences.PROFILE_BODYWEIGHT);
            } else if (checkedId == R.id.rbHomeDumbbells) {
                prefs.setEquipmentProfile(UserPreferences.PROFILE_HOME_DUMBBELLS);
            } else if (checkedId == R.id.rbGarage) {
                prefs.setEquipmentProfile(UserPreferences.PROFILE_GARAGE);
            } else if (checkedId == R.id.rbFullGym) {
                prefs.setEquipmentProfile(UserPreferences.PROFILE_FULL_GYM);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}