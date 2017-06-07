package com.mg.appupdate;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * @author rahul
 */
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

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        SharedPreferences prefs = this.context.getSharedPreferences(
                ReactNativeAppUpdate.RNAU_SHARED_PREFERENCES, Context.MODE_PRIVATE
        );
        String version =  prefs.getString(ReactNativeAppUpdate.RNAU_STORED_VERSION, null);
        constants.put("jsCodeVersion", version);
        return constants;
    }
}
