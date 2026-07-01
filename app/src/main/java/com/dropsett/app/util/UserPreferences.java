package com.dropsett.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferences {

    private static final String PREFS_NAME    = "dropsett_prefs";
    private static final String KEY_EQUIPMENT = "equipment_profile";

    // Equipment profile constants
    public static final String PROFILE_BODYWEIGHT = "Bodyweight";
    public static final String PROFILE_HOME_DUMBBELLS = "Home_Dumbbells";
    public static final String PROFILE_GARAGE = "Garage";
    public static final String PROFILE_FULL_GYM = "Full_Gym";

    private final SharedPreferences prefs;

    public UserPreferences(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getEquipmentProfile() {
        return prefs.getString(KEY_EQUIPMENT, PROFILE_FULL_GYM);
    }

    public void setEquipmentProfile(String profile) {
        prefs.edit().putString(KEY_EQUIPMENT, profile).apply();
    }

    public boolean hasSetEquipmentProfile() {
        return prefs.contains(KEY_EQUIPMENT);
    }

    // Returns the list of equipment type strings that should be
    // visible given the current profile — used to filter exercises
    public List<String> getAllowedEquipmentTypes() {
        String profile = getEquipmentProfile();
        switch (profile) {
            case PROFILE_BODYWEIGHT:
                return Arrays.asList("Bodyweight", "Band");

            case PROFILE_HOME_DUMBBELLS:
                return Arrays.asList("Bodyweight", "Band",
                        "Dumbbell", "Kettlebell");

            case PROFILE_GARAGE:
                return Arrays.asList("Bodyweight", "Band",
                        "Dumbbell", "Kettlebell",
                        "Barbell");

            case PROFILE_FULL_GYM:
            default:
                return Arrays.asList("Bodyweight", "Band",
                        "Dumbbell", "Kettlebell",
                        "Barbell", "Cable", "Machine");
        }
    }
}