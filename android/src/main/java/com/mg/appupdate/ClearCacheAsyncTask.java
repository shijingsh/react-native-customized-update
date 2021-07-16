package com.mg.appupdate;



import android.os.AsyncTask;

import com.facebook.react.bridge.Callback;

public class ClearCacheAsyncTask extends AsyncTask<Integer,Integer,String> {
    public ReactNativeAppUpdateModule myclearCacheModule = null;
    public Callback callback;
    public ClearCacheAsyncTask(ReactNativeAppUpdateModule clearCacheModule, Callback callback) {
        super();
        this.myclearCacheModule = clearCacheModule;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.invoke();

    }

    @Override
    protected String doInBackground(Integer... params) {
        myclearCacheModule.clearCache();
        return null;
    }


}
