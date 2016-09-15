package com.example.raaja.applockui;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;

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

    public static final int USAGE_ACCESS_PERMISSION_REQUEST=3;
    private static final String USAGE_ACCESS_DIALOG_TAG = "usageAccessPermissionDialog";

    Toolbar appLockActivityToolbar;
    RecyclerView appLockRecyclerView;
    AppLockRecyclerAdapter appLockRecyclerAdapter;
    ArrayList<String> includedSystemApps,recommendedAppList;
    TreeMap<String,String> installedAppMap, checkedAppMap;
    TreeMap<String ,Boolean> recommendedAppMap;
    private AppLockModel appLockModel;
    private boolean usagePermissionGranted;


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
        appLockActivityToolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("AppLock");
        loadAppsAtStart();
        calculateMarginHeader();
        checkAndSetUsagePermissions();

    }

    void setUsageAccessPermissionGranted(boolean granted){
        this.usagePermissionGranted = granted;
    }

    boolean getUsageAccessPermissionGranted(){
        return this.usagePermissionGranted;
    }

    private void loadAppsAtStart(){
        installedAppMap = appLockModel.getInstalledAppsMap();
        checkedAppMap = appLockModel.getCheckedAppsMap();
        recommendedAppMap = appLockModel.getRecommendedAppsMap();
        for(String entries: recommendedAppList){
            if(!recommendedAppMap.containsKey(entries)){
                recommendedAppMap.put(entries,false);
            }
        }
        PackageManager pkgManager = getPackageManager();
        ArrayList<ApplicationInfo> totalSystemApps =
                new ArrayList<>(pkgManager.getInstalledApplications(PackageManager.GET_META_DATA));
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
        appLockRecyclerAdapter = new AppLockRecyclerAdapter(appLockModel, this);
        appLockRecyclerView.setAdapter(appLockRecyclerAdapter);
        appLockRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
    }

    void calculateMarginHeader(){
       DisplayMetrics metrices =  getResources().getDisplayMetrics();
        AppLockRecyclerAdapter.HEADER_MARGIN_SIZE_TEN = 10 * (metrices.densityDpi/DisplayMetrics.DENSITY_DEFAULT);
        AppLockRecyclerAdapter.HEADER_MARGIN_SIZE_FIFTEEN = 15 * (metrices.densityDpi/DisplayMetrics.DENSITY_DEFAULT);
    }

    void startUsagePermissionDialog(){
        DialogFragment usageDialog = new GrantUsageAccessDialog();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        usageDialog.show(fragmentTransaction,USAGE_ACCESS_DIALOG_TAG);
    }

    @TargetApi(21)
    void checkAndSetUsagePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager opsManager = (AppOpsManager) getApplicationContext().getSystemService(APP_OPS_SERVICE);
            if (opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName()) == AppOpsManager.MODE_ALLOWED) {
                setUsageAccessPermissionGranted(true);
            } else {
                setUsageAccessPermissionGranted(false);
            }
        }
    }

    @TargetApi(21)
    void startUsageAccessSettingActivity(){
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),USAGE_ACCESS_PERMISSION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == USAGE_ACCESS_PERMISSION_REQUEST){
            checkAndSetUsagePermissions();
        }
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getUsageAccessPermissionGranted()){
            startService(new Intent(this,AppLockingService.class));
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            startService(new Intent(this,AppLockingService.class));
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
        Log.d(TAG," " + String .valueOf(appLockRecyclerAdapter == null));
        Log.d(TAG,"Called onDestroy");
    }
}
