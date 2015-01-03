package com.lambertsoft.yambaapp;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }
    @Override
    public void onHandleIntent(Intent intent) {
        Log.d(TAG, "onStarted");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please update username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        YambaClient cloud = new YambaClient(username, password);
        try {
            List<YambaClient.Status> timeline = cloud.getTimeline(20);
            for (YambaClient.Status status : timeline) {
                Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
            }
        } catch (YambaClientException e) {
                Log.e(TAG, "Failed to fetch the timeline", e);
        }
        return;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
