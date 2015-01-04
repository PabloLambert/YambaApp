package com.lambertsoft.yambaapp;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class TimeLineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = TimeLineFragment.class.getName();
    private static final String[] FROM = {StatusContract.Column.USER, StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT};
    private static final int[] TO = {R.id.list_item_text_user, R.id.list_item_text_message, R.id.list_item_text_created_at};
    private SimpleCursorAdapter mAdapter;
    private static final int LOADER_ID = 42;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_time_line, null, FROM, TO, 0);
        mAdapter.setViewBinder(VIEW_BINDER);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID,null, this);
    }

    private final static SimpleCursorAdapter.ViewBinder VIEW_BINDER = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            long timestamp;

            switch (view.getId()) {
                case R.id.list_item_text_created_at:
                    timestamp = cursor.getLong(columnIndex);
                    CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
                    ((TextView) view).setText(relTime);
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) { // 4
        if (id != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");

        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT); // 5
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) { // 6
        Log.d(TAG, "onLoadFinished with cursor: " + cursor.getCount());
        mAdapter.swapCursor(cursor); // 7
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { // 8
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id ){
        DetailFragment fragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.fragment_detail);
        if (fragment != null && fragment.isVisible()) {
            fragment.updateView(id);
        } else {
            startActivity(new Intent(getActivity(), DetailActivity.class).putExtra(StatusContract.Column.ID, id));
        }
    }

}
