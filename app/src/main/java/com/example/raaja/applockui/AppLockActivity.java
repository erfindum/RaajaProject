package com.example.raaja.applockui;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by RAAJA on 09-09-2016.
 */
public class AppLockActivity extends AppCompatActivity {
    private static final String TAG = "AppLockActivity ";

    Toolbar appLockActivityToolbar;
    RecyclerView appLockRecyclerView;
    AppLockRecyclerAdapter appLockRecyclerAdapter;
    ArrayList<String> includedSystemApps,recommendedAppList;
    TreeMap<String,String> installedAppMap, checkedAppMap;
    TreeMap<String ,Boolean> recommendedAppMap;
    private AppLockModel appLockModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_lock_activity);
        appLockModel = new AppLockModel(this.getSharedPreferences(AppLockModel.APP_LOCK_PREFERENCE_NAME,MODE_PRIVATE));
        appLockRecyclerView = (RecyclerView) findViewById(R.id.app_lock_activity_recycler_view);
        appLockActivityToolbar = (Toolbar) findViewById(R.id.app_lock_activity_tool_bar);
        List<String> arrayResource = Arrays.asList(getResources().getStringArray(R.array.includedSystemApps));
        includedSystemApps = new ArrayList<>(arrayResource);
        List<String> array1Resource = Arrays.asList(this.getResources().getStringArray(R.array.recommended_app_list));
        recommendedAppList = new ArrayList<>(array1Resource);
        setSupportActionBar(appLockActivityToolbar);
        getSupportActionBar().setTitle("AppLock");
        loadAppsAtStart();
        calculateMarginHeader();;

    }

    private void loadAppsAtStart(){
        installedAppMap = appLockModel.getInstalledAppsMap();
        checkedAppMap = appLockModel.getCheckedAppsMap();
        recommendedAppMap = appLockModel.getRecommendedAppsMap();
        for(Map.Entry<String,String> entry : installedAppMap.entrySet()){
            Log.d(TAG,"Installed :" + entry.getValue());
        }
        for(Map.Entry<String,String> entry : checkedAppMap.entrySet()){
            Log.d(TAG,"Checked :" + entry.getValue());
        }
        for(String entries: recommendedAppList){
            if(!recommendedAppMap.containsKey(entries)){
                recommendedAppMap.put(entries,false);
            }
        }
        PackageManager pkgManager = getPackageManager();
        ArrayList<ApplicationInfo> totalSystemApps =
                new ArrayList<>(pkgManager.getInstalledApplications(PackageManager.GET_META_DATA));
            Log.d("Model","Added Called Full list" );
            for(ApplicationInfo appInfo:totalSystemApps){
                if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1 &&
                        !installedAppMap.containsKey(appInfo.packageName) && !checkedAppMap.containsKey(appInfo.packageName)){
                    installedAppMap.put(appInfo.packageName,pkgManager.getApplicationLabel(appInfo).toString());
                }else
                if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1 &&
                        !installedAppMap.containsKey(appInfo.packageName) && includedSystemApps.contains(appInfo.packageName) &&
                        !checkedAppMap.containsKey(appInfo.packageName)){
                    installedAppMap.put(appInfo.packageName,pkgManager.getApplicationLabel(appInfo).toString());
                }
            }
            if(installedAppMap.containsKey(getPackageName())){
                installedAppMap.remove(getPackageName());
            }

            appLockModel.updateRecommendedAppPackages(recommendedAppMap);
             appLockModel.updateAppPackages(installedAppMap,AppLockModel.INSTALLED_APPS_PACKAGE);
            appLockModel.loadAppPackages(AppLockModel.RECOMMENDED_APPS_PACKAGE);
            int loadComplete = appLockModel.loadAppPackages(AppLockModel.INSTALLED_APPS_PACKAGE);
            if(loadComplete==AppLockModel.APP_LIST_UPDATED) {
                displayRecyclerView();
            }
    }


    private void displayRecyclerView(){
        appLockRecyclerAdapter = new AppLockRecyclerAdapter(appLockModel, getPackageManager(),getSharedPreferences(AppLockModel.APP_LOCK_PREFERENCE_NAME,MODE_PRIVATE));
        appLockRecyclerView.setAdapter(appLockRecyclerAdapter);
        appLockRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
    }

    void calculateMarginHeader(){
       DisplayMetrics metrices =  getResources().getDisplayMetrics();
        AppLockRecyclerAdapter.INSTALLED_HEADER_MARGIN_SIZE_TEN = 10 * (metrices.densityDpi/DisplayMetrics.DENSITY_DEFAULT);
        AppLockRecyclerAdapter.INSTALLED_HEADER_MARGIN_SIZE_FIFTEEN = 15 * (metrices.densityDpi/DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"Called onStart");
        if(appLockRecyclerAdapter!=null){
            appLockRecyclerAdapter.updateAppLockRecyclerAdapter();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appLockRecyclerAdapter!=null) {
            appLockRecyclerAdapter.closeAppLockRecyclerAdapter();
            appLockRecyclerAdapter.notifyDataSetChanged();
            Log.d(TAG,"");
        }
        Log.d(TAG,"Called onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"Called onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Called onDestroy");
    }
}
