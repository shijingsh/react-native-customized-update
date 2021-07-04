package com.mg.appupdate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReactNativeAppUpdate {
    /**singleton instance*/
    private static ReactNativeAppUpdate ourInstance = new ReactNativeAppUpdate();
    private  ReactNativeAppUpdateActivity appUpdateActivity;

    public static final String RN_SHARED_PREFERENCES = "React_Native_App_Updater_Shared_Preferences";
    public static final String RN_STORED_VERSION = "React_Native_App_Updater_Stored_Version";
    private final String RN_LAST_UPDATE_TIMESTAMP = "React_Native_App_Updater_Last_Update_Timestamp";
    private final String RN_STORED_JS_FILENAME = "main.android.jsbundle";
    private final String RN_STORED_APK_FILENAME = "appUpdateTem.apk";
    private final String RN_STORED_JS_FOLDER = "jsCode";
    private final String RN_STORED_APK_FOLDER = "tempApk";
    private String metadataAssetName ="metadata.android.json";

    private String checkVersionUrl;
    private ReactNativeAppUpdaterFrequency updateFrequency = ReactNativeAppUpdaterFrequency.EACH_TIME;
    private Context context;
    private boolean showProgress = true;
    private JSONObject metadata = new JSONObject();
    private String jSBundleFilePath = null;

    public static ReactNativeAppUpdate getInstance(Context context) {
        ourInstance.context = context;
        return ourInstance;
    }
    private ReactNativeAppUpdate() {
    }

    public ReactNativeAppUpdate setCheckVersionUrl(String url) {
        this.checkVersionUrl = url;
        return this;
    }
    public ReactNativeAppUpdate setUpdateFrequency(ReactNativeAppUpdaterFrequency frequency) {
        this.updateFrequency = frequency;
        return this;
    }
    public ReactNativeAppUpdate showProgress(boolean progress) {
        this.showProgress = progress;
        return this;
    }

    public ReactNativeAppUpdate checkForUpdates() {
        if (this.shouldCheckForUpdates()) {
            checkUpdate();
        }
        return this;
    }

    public boolean checkUpdate(){
        //读取js版本
        getLatestJsVersion();
        //this.showProgressToast(R.string.auto_updater_checking);
        FetchMetadataTask task = new FetchMetadataTask();
        if(this.checkVersionUrl!=null){
            task.execute(this.checkVersionUrl);
        }

        return false;
    }

    private boolean shouldCheckForUpdates() {
        //每次启动时检查更新
        if (this.updateFrequency == ReactNativeAppUpdaterFrequency.EACH_TIME) {
            return true;
        }

        SharedPreferences prefs = context.getSharedPreferences(RN_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        long msSinceUpdate = System.currentTimeMillis() - prefs.getLong(RN_LAST_UPDATE_TIMESTAMP, 0);
        int daysSinceUpdate = (int) (msSinceUpdate / 1000 / 60 / 60 / 24);

        switch (this.updateFrequency) {
            case DAILY:
                return daysSinceUpdate >= 1;
            case WEEKLY:
                return daysSinceUpdate >= 7;
            case MONTHLY:
                return daysSinceUpdate >= 30;
            default:
                return true;
        }
    }

    public String getLatestJsVersion() {
        SharedPreferences prefs = context.getSharedPreferences(RN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String currentVersionStr = prefs.getString(RN_STORED_VERSION, null);
        if(currentVersionStr!=null&&currentVersionStr.trim().length()>0){
            return currentVersionStr;
        }

        String jsonString = this.getStringFromAsset(this.metadataAssetName);
        if (jsonString == null) {
            return null;
        } else {
            try {
                JSONObject assetMetadata = new JSONObject(jsonString);
                String assetVersionStr = assetMetadata.getString("jsVersion");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(RN_STORED_VERSION, assetVersionStr);
                editor.apply();
                return assetVersionStr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // update check time
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(RN_LAST_UPDATE_TIMESTAMP, new Date().getTime());
        editor.apply();

        return null;
    }

    private String getStringFromAsset(String assetName) {
        String jsonString = null;
        try {
            InputStream inputStream = this.context.getAssets().open(assetName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public boolean shouldJsUpdate(boolean isUpdateNow) {

        String jsVersionStr = getMetadata("jsVersion");
        if(jsVersionStr==null)return false;
        boolean shouldUpdate = false;
        //判断js版本
        SharedPreferences prefs = context.getSharedPreferences(RN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String currentVersionStr = prefs.getString(RN_STORED_VERSION, null);
        if (currentVersionStr == null) {
            shouldUpdate = true;
        } else {
            Version currentVersion = new Version(currentVersionStr);
            Version updateVersion = new Version(jsVersionStr);
            if (currentVersion.compareTo(updateVersion) < 0) {
                shouldUpdate = true;
            }
        }

        return shouldUpdate;
    }

    public boolean shouldApkUpdate(boolean isUpdateNow) {
        if(isUpdateNow){
            if(checkVersionUrl != null) {
                this.metadata = fetchMetaData(this.checkVersionUrl);
            }
        }
        String version = getMetadata("version");
        if(version==null)return false;
        String currentApkVersionStr = this.getContainerVersion();
        Version currentApkVersion = new Version(currentApkVersionStr);
        Version apkVersion = new Version(version);

        return currentApkVersion.compareTo(apkVersion) < 0;
    }

    private String getMetadata(String name){
        String value = null;
        try {
            value = metadata.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    private String getContainerVersion() {
        String version = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private void showProgressToast(int message) {
        if (this.showProgress && context.getResources().getString(message).length() > 0) {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }
    }

    private JSONObject fetchMetaData(String checkVersionUrl){
        JSONObject metadata  = new JSONObject();
        try {
            URL url = new URL(checkVersionUrl);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String metadataStr = response.body().string();
            if (!metadataStr.isEmpty()) {
                metadata = new JSONObject(metadataStr);
            } else {
                ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_no_metadata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metadata;
    }

    private class FetchMetadataTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject metadata  = fetchMetaData(params[0]);
            return metadata;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            ReactNativeAppUpdate.this.metadata = jsonObject;
        }
    }

    private class FetchJsUpdateTask extends AsyncTask<String, Void, String> {

        private PowerManager.WakeLock mWakeLock;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_downloading);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;
            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();
                File jsCodeDir = context.getDir(RN_STORED_JS_FOLDER, Context.MODE_PRIVATE);
                if (!jsCodeDir.exists()) {
                    jsCodeDir.mkdirs();
                }
                File jsCodeFile = new File(jsCodeDir, RN_STORED_JS_FILENAME);
                output = new FileOutputStream(jsCodeFile);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    output.write(data, 0, count);
                }

                SharedPreferences prefs = context.getSharedPreferences(RN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(RN_STORED_VERSION, params[1]);
                editor.putLong(RN_LAST_UPDATE_TIMESTAMP, new Date().getTime());
                editor.apply();
                //保存bundle 文件的位置
                jSBundleFilePath = jsCodeFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            appUpdateActivity.loadBundle();
            mWakeLock.release();
            if (result != null) {
                ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_downloading_error);
            } else {
                ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_downloading_success);
            }
        }
    }

    private class FetchApkUpdateTask extends AsyncTask<String, Void, String> {

        private PowerManager.WakeLock mWakeLock;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_downloading);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;
            String filePath = null;
            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();
                File jsCodeDir = context.getDir(RN_STORED_APK_FOLDER, Context.MODE_PRIVATE);
                if (!jsCodeDir.exists()) {
                    jsCodeDir.mkdirs();
                }
                File jsCodeFile = new File(jsCodeDir, RN_STORED_APK_FILENAME);
                output = new FileOutputStream(jsCodeFile);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    output.write(data, 0, count);
                }

                filePath = jsCodeFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(String file) {
            installApkUpdate(file);
            mWakeLock.release();
            if (file == null) {
                ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_downloading_error);
            } else {
                ReactNativeAppUpdate.this.showProgressToast(R.string.auto_updater_downloading_success);
            }
        }
    }

    /**
     * js bundle file
     * @return
     */
    public String getJSBundleFile() {

        return jSBundleFilePath;
    }

    public void installApkUpdate(String file) {
        if(file==null)return;
        String cmd = "chmod 777 " + file;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setDataAndType(Uri.parse("file://" + file), "application/vnd.android.package-archive");
        //FileProvider7
        File fileObj = new File(file);
        FileProvider7.setIntentDataAndType(this.context,
                intent, "application/vnd.android.package-archive", fileObj, true);

        ourInstance.context.startActivity(intent);
    }

    public void jsUpdate() {
        String jsUrl = getMetadata("jsUrl");
        String version = getMetadata("jsVersion");
        if(jsUrl==null || version==null)return;
        FetchJsUpdateTask updateTask = new FetchJsUpdateTask();
        updateTask.execute(jsUrl, version);
    }

    public void apkUpdate() {
        String apkUrl = getMetadata("url");
        String version = getMetadata("version");
        if(apkUrl==null || version==null)return;
        FetchApkUpdateTask updateTask = new FetchApkUpdateTask();
        updateTask.execute(apkUrl, version);
    }

    public void setAppUpdateActivity(ReactNativeAppUpdateActivity appUpdateActivity) {
        this.appUpdateActivity = appUpdateActivity;
    }

    public String getLatestJSCodeLocation() {
        SharedPreferences prefs = context.getSharedPreferences(RN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String currentVersionStr = prefs.getString(RN_STORED_VERSION, "1.0.0");

        Version currentVersion;
        try {
            currentVersion = new Version(currentVersionStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String jsonString = this.getStringFromAsset(this.metadataAssetName);
        if (jsonString == null) {
            return null;
        } else {
            String jsCodePath = null;
            try {
                JSONObject assetMetadata = new JSONObject(jsonString);
                String assetVersionStr = assetMetadata.getString("jsVersion");
                Version assetVersion = new Version(assetVersionStr);

                if (currentVersion.compareTo(assetVersion) > 0) {
                    File jsCodeDir = context.getDir(RN_STORED_JS_FOLDER, Context.MODE_PRIVATE);
                    File jsCodeFile = new File(jsCodeDir, RN_STORED_JS_FILENAME);
                    jsCodePath = jsCodeFile.getAbsolutePath();
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(RN_STORED_VERSION, currentVersionStr);
                    editor.apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsCodePath;
        }
    }

}
