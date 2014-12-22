package com.lambertsoft.yambaapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;

import org.w3c.dom.Text;


public class StatusActivity extends ActionBarActivity {

    private final static String TAG = "StatusActivity";
    EditText editStatus;
    Button buttonTweet;
    TextView textCount;
    int defaultTextColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        editStatus = (EditText) findViewById(R.id.editStatus);
        buttonTweet = (Button) findViewById(R.id.buttonTweet);
        textCount = (TextView) findViewById(R.id.textCount);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
