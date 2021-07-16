package com.mg.appupdate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ReactNativeAppUpdateModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext context;

    static public ReactNativeAppUpdateModule myclearCacheModule;

    public ReactNativeAppUpdateModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
        myclearCacheModule = this;
    }

    @Override
    public String getName() {
        return "ReactNativeCustomizedUpdate";
    }

    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        SharedPreferences prefs = this.context.getSharedPreferences(
                ReactNativeAppUpdate.RN_SHARED_PREFERENCES, Context.MODE_PRIVATE
        );
        String version =  prefs.getString(ReactNativeAppUpdate.RN_STORED_VERSION, null);
        constants.put("jsCodeVersion", version);
        return constants;
    }

    @ReactMethod
    public void shouldApkUpdate(final ReadableMap options) {
        final Activity activity = getCurrentActivity();
        final ReactNativeAppUpdate update = ReactNativeAppUpdate.getInstance(activity);
        final boolean isUpdateNow = options.hasKey("isUpdateNow") && options.getBoolean("isUpdateNow");
        String checkVersionUrl = "";
        if(options.hasKey("checkVersionUrl") ){
            checkVersionUrl = options.getString("checkVersionUrl");
            update.setCheckVersionUrl(checkVersionUrl);
        }

        if(update.shouldApkUpdate(isUpdateNow)){
            try {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle(R.string.auto_updater_downloaded_title);
                alertDialogBuilder
                        .setMessage(R.string.auto_updater_downloaded_message)
                        .setCancelable(false)
                        .setPositiveButton(
                                R.string.auto_updater_downloaded_now,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        update.apkUpdate();
                                    }
                                }
                        )
                        .setNegativeButton(
                                R.string.auto_updater_downloaded_later,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(update.shouldJsUpdate(isUpdateNow)){
            try {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle(R.string.auto_updater_downloaded_title);
                alertDialogBuilder
                        .setMessage(R.string.auto_updater_downloaded_message)
                        .setCancelable(false)
                        .setPositiveButton(
                                R.string.auto_updater_downloaded_now,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        update.jsUpdate();
                                    }
                                }
                        )
                        .setNegativeButton(
                                R.string.auto_updater_downloaded_later,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //获取缓存大小
    @ReactMethod
    public void getAppCacheSize(Callback callback) {
        // 计算缓存大小
        long fileSize = 0;
        File filesDir = getReactApplicationContext().getFilesDir();// /data/data/package_name/files
        File cacheDir = getReactApplicationContext().getCacheDir();// /data/data/package_name/cache
        fileSize += CacheUtils.getDirSize(filesDir);
        fileSize += CacheUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (CacheUtils.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = CacheUtils.getExternalCacheDir(getReactApplicationContext());//"<sdcard>/Android/data/<package_name>/cache/"
            fileSize += CacheUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0) {
            String strFileSize = CacheUtils.formatFileSize(fileSize);
            String unit = CacheUtils.formatFileSizeName(fileSize);
            callback.invoke(strFileSize, unit);
        } else {
            WritableMap params = Arguments.createMap();
            callback.invoke("0", "B");
        }
    }

    //清除缓存
    @ReactMethod
    public void clearAppCache(Callback callback) {
        ClearCacheAsyncTask asyncTask = new ClearCacheAsyncTask(myclearCacheModule, callback);
        asyncTask.execute(10);
    }

    /**
     * 清除app缓存
     */
    public  void clearCache() {

        getReactApplicationContext().deleteDatabase("webview.db");
        getReactApplicationContext().deleteDatabase("webview.db-shm");
        getReactApplicationContext().deleteDatabase("webview.db-wal");
        getReactApplicationContext().deleteDatabase("webviewCache.db");
        getReactApplicationContext().deleteDatabase("webviewCache.db-shm");
        getReactApplicationContext().deleteDatabase("webviewCache.db-wal");
        //清除数据缓存
        CacheUtils.clearCacheFolder(getReactApplicationContext().getFilesDir(), System.currentTimeMillis());
        CacheUtils.clearCacheFolder(getReactApplicationContext().getCacheDir(), System.currentTimeMillis());
        //2.2版本才有将应用缓存转移到sd卡的功能
        if (CacheUtils.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            CacheUtils.clearCacheFolder(CacheUtils.getExternalCacheDir(getReactApplicationContext()), System.currentTimeMillis());
        }

    }
}
