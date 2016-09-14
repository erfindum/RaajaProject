package com.example.raaja.applockui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by RAAJA on 07-09-2016.
 */
public class AppLoaderActivity extends AppCompatActivity {

    private static final int REQUEST_START_ACTIVITY_FIRST_LOAD =2;
    private SharedPreferences prefs;
    private boolean isFirstLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_loader_activity);
        prefs = getSharedPreferences(AppLockModel.APP_LOCK_PREFERENCE_NAME,MODE_PRIVATE);
        isFirstLoad = prefs.getBoolean("lockUp_is_first_load",true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLoad){
            startActivityForResult(new Intent(this,SetPinPatternActivity.class),REQUEST_START_ACTIVITY_FIRST_LOAD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_START_ACTIVITY_FIRST_LOAD){
            finish();
        }
    }
}
