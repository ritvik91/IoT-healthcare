package com.example.android.responder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class showCurTemp extends AppCompatActivity {

    String pid;
    JSONParser jParser = new JSONParser();
    TextView tvCurTemp;
    ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cur_temp);

        Intent i = getIntent();
        pid = i.getStringExtra("pid");

        tvCurTemp = (TextView) findViewById(R.id.tvCurTemp);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // Hit WebService
                new GetPatientList().execute();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        scheduler.shutdown();
        super.onDestroy();
    }

    class GetPatientList extends AsyncTask<String, String, String> {

        JSONArray jsonArray;
        String jsonString;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(WelcomeDoc.this);
//            pDialog.setMessage("Getting Patients' Data. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("doc", "123"));
            //params.add(new BasicNameValuePair("func", "test"));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(getString(R.string.url_patient_info_doc)+"?func=getCurrTemp&id="+pid, "POST", params);

            // Check your log cat for JSON reponse
            try {
                jsonString = json.get("Temp").toString();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tvCurTemp.setText(jsonString);
                    }
                });
                Log.d("Info: ", jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
//            if(pDialog != null) {
//                pDialog.dismiss();
//                pDialog = null;
//            }
        }
    }
}
