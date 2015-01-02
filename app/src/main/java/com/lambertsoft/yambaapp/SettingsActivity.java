package com.lambertsoft.yambaapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by PLambert on 02-01-2015.
 */
public class SettingsActivity extends Activity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            SettingsFragment fragment = new SettingsFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getName())
                    .commit();
        }
    }
}
