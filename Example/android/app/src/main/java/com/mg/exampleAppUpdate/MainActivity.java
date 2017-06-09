package com.mg.exampleAppUpdate;

import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.mg.appupdate.*;

import java.util.Arrays;
import java.util.List;
import com.mg.appupdate.ReactNativeAppUpdate.ReactNativeAutoUpdaterFrequency;


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
    protected ReactNativeAutoUpdaterFrequency getUpdateFrequency() {
        return ReactNativeAutoUpdaterFrequency.EACH_TIME;
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
        return "ReactNativeCustomizedUpdater";
    }

    /**
     * Returns whether dev mode should be enabled.
     * This enables e.g. the dev menu.
     */
    @Override
    protected boolean getUseDeveloperSupport() {
        return true;
    }

    /**
     * A list of packages used by the app. If the app uses additional views
     * or modules besides the default ones, add more packages here.
     */
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
                new ReactNativeAppUpdatePackage(),
                new MainReactPackage());
    }

}
