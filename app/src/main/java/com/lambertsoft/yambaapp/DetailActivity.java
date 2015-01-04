package com.lambertsoft.yambaapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by InnovaTI on 04-01-15.
 */
public class DetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null ) {
            DetailFragment detailFragment = new DetailFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, detailFragment, detailFragment.getClass().getSimpleName())
                    .commit();

        }
    }
}
