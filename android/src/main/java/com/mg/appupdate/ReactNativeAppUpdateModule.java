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

import java.util.HashMap;
import java.util.Map;


public class ReactNativeAppUpdateModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext context;

    public ReactNativeAppUpdateModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
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
        String version =  prefs.getString(ReactNativeAppUpdate.RN_STORED_JS_VERSION, null);
        constants.put("jsCodeVersion", version);
        return constants;
    }

    @ReactMethod
    public void shouldApkUpdate(final ReadableMap options) {
        final Activity activity = getCurrentActivity();
        final ReactNativeAppUpdate update = ReactNativeAppUpdate.getInstance(activity);
        final boolean isUpdateNow = options.hasKey("isUpdateNow") && options.getBoolean("isUpdateNow");
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
        }
    }

    @ReactMethod
    public void shouldJsUpdate(final ReadableMap options) {
        final Activity activity = getCurrentActivity();
        final ReactNativeAppUpdate update = ReactNativeAppUpdate.getInstance(activity);
        final boolean isUpdateNow = options.hasKey("isUpdateNow") && options.getBoolean("isUpdateNow");
        if(update.shouldJsUpdate(isUpdateNow)){
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

}
