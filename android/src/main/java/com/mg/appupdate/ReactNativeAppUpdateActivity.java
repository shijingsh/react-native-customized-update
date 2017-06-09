package com.mg.appupdate;

import android.os.Bundle;

import com.facebook.react.ReactActivity;

/**
 * @author rahul
 */
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

    protected ReactNativeAppUpdaterFrequency getUpdateFrequency() {
        return ReactNativeAppUpdaterFrequency.EACH_TIME;
    }

    protected boolean getShowProgress() {
        return true;
    }

}
