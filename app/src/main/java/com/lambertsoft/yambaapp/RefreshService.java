package com.lambertsoft.yambaapp;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";
    static YambaClientException error;
    android.os.Handler handler;

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
        handler = new android.os.Handler();
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

        ContentValues values = new ContentValues();

        YambaClient cloud =  new YambaClient(username, password);
        try {
            int count = 0;

            List<YambaClient.Status> timeline = cloud.getTimeline(20);

            for (YambaClient.Status status : timeline) {
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getMessage());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());

                Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                if (uri != null ){
                    count ++;
                }

                //Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
            }
            if (count > 0) {
                sendBroadcast(new Intent("com.lambertsoft.yambaapp.action.NEW_STATUS").putExtra("count", count));
            }
        } catch (YambaClientException e) {
             error = e;

            handler.post(new Runnable() {
                //@Override
                public void run() {
                    Toast.makeText(RefreshService.this, "Failed to fetch the timeline" + error , Toast.LENGTH_SHORT).show();
                }
            });
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
