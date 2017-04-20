package com.example.android.responder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class showTempHist extends AppCompatActivity {

    String pid;
    JSONParser jParser = new JSONParser();
    ScheduledExecutorService scheduler;
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_temp_hist);

        Intent i = getIntent();
        pid = i.getStringExtra("pid");

        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("EKG");

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(550);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(6);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // Hit WebService
                new GetTempHist().execute();
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    @Override
    public void onDestroy() {
        scheduler.shutdown();
        super.onDestroy();
    }

    class GetTempHist extends AsyncTask<String, String, String> {

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
            JSONObject json = jParser.makeHttpRequest(getString(R.string.url_patient_info_doc)+"?func=getEKG&id="+pid, "POST", params);

            // Check your log cat for JSON reponse
            try {
                jsonString = json.get("EKG").toString();
                Log.d("EKG", jsonString);
                final String[] tempHistArray = jsonString.split(",");
                final DataPoint[] dp = new DataPoint[tempHistArray.length];
                for(int i=0; i<tempHistArray.length; i++)
                    dp[i] = new DataPoint((i+1)*0.08, Integer.parseInt(tempHistArray[i]));
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
                        graph.removeAllSeries();
                        graph.addSeries(series);
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
