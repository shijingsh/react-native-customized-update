package com.mg.appupdate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.JSBundleLoader;

import java.lang.reflect.Field;

public abstract class ReactNativeAppUpdateActivity extends ReactActivity {

    private ReactNativeAppUpdate updater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updater = ReactNativeAppUpdate.getInstance(this.getApplicationContext());
        updater.setCheckVersionUrl(this.getCheckVersionUrl())
                .setUpdateFrequency(this.getUpdateFrequency())
                .showProgress(this.getShowProgress())
                .checkForUpdates()
                .setAppUpdateActivity(this);
    }


    protected abstract String getCheckVersionUrl();

    protected void loadBundle() {
        final ReactInstanceManager instanceManager;
        try {

            instanceManager = resolveInstanceManager();
            if (instanceManager == null) {
                return;
            }

            //获取本地的js代码 这里就不给出代码了。 如果本地没有就返回assets目录的
//            String latestJSBundleFile = Utils.getJSBundleFileInternal();
            ReactNativeAppUpdate updater = ReactNativeAppUpdate.getInstance(this.getApplication());
            String latestJSBundleFile = updater.getLatestJSCodeLocation();

            setJSBundle(instanceManager, latestJSBundleFile);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {

                        instanceManager.recreateReactContextInBackground();
                    } catch (Exception e) {
                        // The recreation method threw an unknown exception
                        // so just simply fallback to restarting the Activity (if it exists)
                        loadBundleLegacy();
                    }
                }
            });
        }  catch (Exception e) {
            e.printStackTrace();
            loadBundleLegacy();
        }
    }


    protected ReactNativeAppUpdaterFrequency getUpdateFrequency() {
        return ReactNativeAppUpdaterFrequency.EACH_TIME;
    }

    protected boolean getShowProgress() {
        return true;
    }


    private ReactInstanceManager resolveInstanceManager(){
        ReactInstanceManager instanceManager;
        final Activity currentActivity = this;
        if (currentActivity == null) {
            return null;
        }
        ReactApplication reactApplication = (ReactApplication) currentActivity.getApplication();
        instanceManager = reactApplication.getReactNativeHost().getReactInstanceManager();

        return instanceManager;
    }

    private void setJSBundle(ReactInstanceManager instanceManager, String latestJSBundleFile) throws IllegalAccessException {
        try {
            JSBundleLoader latestJSBundleLoader;
            if (latestJSBundleFile.toLowerCase().startsWith("assets://")) {
                latestJSBundleLoader = JSBundleLoader.createAssetLoader(this.getApplicationContext(), latestJSBundleFile, false);
            } else {
                latestJSBundleLoader = JSBundleLoader.createFileLoader(latestJSBundleFile);
            }
            Field bundleLoaderField = instanceManager.getClass().getDeclaredField("mBundleLoader");
            bundleLoaderField.setAccessible(true);
            bundleLoaderField.set(instanceManager, latestJSBundleLoader);
        } catch (Exception e) {
            throw new IllegalAccessException("Could not setJSBundle");
        }
    }

    private void loadBundleLegacy() {
        Log.d("loadBundleLegacy","loadBundle #3 loadBundleLegacy...");
        final Activity currentActivity =  this;
        if (currentActivity == null) {
            // The currentActivity can be null if it is backgrounded / destroyed, so we simply
            // no-op to prevent any null pointer exceptions.
            return;
        }
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.recreate();
            }
        });
    }
}
