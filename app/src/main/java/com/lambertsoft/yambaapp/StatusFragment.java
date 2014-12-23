package com.lambertsoft.yambaapp;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;


public class StatusFragment extends Fragment {

    private final static String TAG = "StatusFragment";
    EditText editStatus;
    Button buttonTweet;
    TextView textCount;
    int defaultTextColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_status, container, false);

        editStatus = (EditText) view.findViewById(R.id.editStatus);
        buttonTweet = (Button) view.findViewById(R.id.buttonTweet);
        textCount = (TextView) view.findViewById(R.id.textCount);
        defaultTextColor = textCount.getTextColors().getDefaultColor();


        buttonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = editStatus.getText().toString();
                Log.d(TAG, "onClick with status: " + status);
                //Toast.makeText(getApplication(), "onClick with status: " + status, Toast.LENGTH_SHORT).show();
                new PostTask().execute(status);
            }
        });

        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int n = 140 - editStatus.length();
                textCount.setText(Integer.toString(n));
                if (n < 10 && n > 0)
                    textCount.setTextColor(Color.YELLOW);
                else if (n < 0 )
                    textCount.setTextColor(Color.RED);
                else
                    textCount.setTextColor(defaultTextColor);
            }
        });

        return view;

    }

    private final class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            YambaClient yambaCloud = new YambaClient("student", "password");
            try {
                yambaCloud.postStatus(params[0]);
                return "Sent OK";
            } catch (Exception e) {
                e.printStackTrace();
                return "Sent Failed!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusFragment.this.getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
