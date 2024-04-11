package com.example.joyserial.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppData {
    private static AppData instance;
    public static final String PREFERENCE_FILE_KEY = "com.example.joyserial.PREFERENCE_FILE_KEY";
    public static boolean IsTerminalOpen = false;
    SharedPreferences sharedPreferences;
    private AppData(Context ctx) {
        sharedPreferences = ctx.getApplicationContext().getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }
    public static  AppData getInstance(Context context) {
        if (instance == null) {
            instance = new AppData(context);
        }
        return instance;
    }

    public void saveBaudRate(int baudRate) {
        if(sharedPreferences == null) Log.v("APP_DATA", "No SharedPreference");
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("baud_rate", baudRate);
            editor.apply();
        }
    }
    public int getBaudRate() {
        return sharedPreferences.getInt("baud_rate", 9600);
    }
}
