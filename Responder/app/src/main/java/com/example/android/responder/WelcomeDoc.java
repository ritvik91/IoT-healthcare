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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WelcomeDoc extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    private ProgressDialog pDialog;
    ListView listView;
    JSONObject json;
    JSONObject schJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_doc);

        new GetPatientList().execute();
    }

    class GetPatientList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WelcomeDoc.this);
            pDialog.setMessage("Getting Patients' Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            String jsonString;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("doc", "123"));
            //params.add(new BasicNameValuePair("func", "test"));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            json = jParser.makeHttpRequest(getString(R.string.url_patient_info_doc)+"?func=getPatientList&doc=123", "POST", params);

            // Check your log cat for JSON reponse
            try {
                jsonString = json.toString();
                Log.d("Info: ", jsonString);
            } catch (NullPointerException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            if(pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }

            listView = (ListView) findViewById(R.id.list);

            try {
                String[] patientList = json.getString("patientList").toString().split(",");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(WelcomeDoc.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, patientList);

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent myIntent = new Intent(view.getContext(), DocsPatientView.class);
                        myIntent.putExtra("pidRequested",listView.getItemAtPosition(position).toString());
                        startActivity(myIntent);
                    }
                });
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }



        }
    }
}
