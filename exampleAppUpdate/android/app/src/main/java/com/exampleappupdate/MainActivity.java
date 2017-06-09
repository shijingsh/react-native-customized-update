package com.exampleappupdate;

import com.mg.appupdate.ReactNativeAppUpdateActivity;
import com.mg.appupdate.ReactNativeAppUpdaterFrequency;

public class MainActivity extends ReactNativeAppUpdateActivity {


    @Override
    protected String getCheckVersionUrl() {
        /**
         * value example:
         * {"jsUrl":"http://192.168.15.67:10086/yourapp/your.js","jsVersion":"1.0","url":"http://192.168.15.67:10086/yourapp/your.apk","version":"2.0"}
         */
        return "http://192.168.15.67:10086/jajayun/app/updateAndroid.json";
        //return "http://www.jajayun.com/app/updateAndroid.json";
    }

    /**
     *  Decide how frequently to check for updates.
     * Available options -
     *  EACH_TIME - each time the app starts
     *  DAILY     - maximum once per day
     *  WEEKLY    - maximum once per week
     * default value - EACH_TIME
     * */
    @Override
    protected ReactNativeAppUpdaterFrequency getUpdateFrequency() {
        return ReactNativeAppUpdaterFrequency.EACH_TIME;
    }

    /**
     *  To show progress during the update process.
     * */
    @Override
    protected boolean getShowProgress() {
        return true;
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "exampleAppUpdate";
    }
}
