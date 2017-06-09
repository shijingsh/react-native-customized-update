package com.mg.appupdate;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.mg.appupdate.ReactNativeAppUpdate.ReactNativeAutoUpdaterFrequency;

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
                .checkForUpdates();
    }

    protected abstract String getCheckVersionUrl();

    protected ReactNativeAutoUpdaterFrequency getUpdateFrequency() {
        return ReactNativeAutoUpdaterFrequency.EACH_TIME;
    }

    protected boolean getShowProgress() {
        return true;
    }

}
