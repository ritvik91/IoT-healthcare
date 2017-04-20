package com.example.android.responder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WelcomeNurse extends AppCompatActivity {
    final Context context = this;
    JSONParser jParser = new JSONParser();
    EditText etPid, etMed, etDid;
    Spinner spTime;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_nurse);

        addListenerOnSpinnerItemSelection();
        
        Button btnEnterSch = (Button) findViewById(R.id.btnEnterSch);
        Button btnViewSch = (Button) findViewById(R.id.btnViewSch);
        Button btnViewVitals = (Button) findViewById(R.id.btnViewVitals);

        btnEnterSch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_insert_patient_info, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                spTime = (Spinner) promptsView.findViewById(R.id.spinnerPeriod);
                etPid = (EditText) promptsView.findViewById(R.id.etPid);
                etMed = (EditText) promptsView.findViewById(R.id.etMed);
                etDid = (EditText) promptsView.findViewById(R.id.etDid);

                spTime.setOnItemSelectedListener(new CustomOnItemSelectedListener());

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        Toast.makeText(context, String.valueOf(spTime.getSelectedItem()), Toast.LENGTH_SHORT).show();
                                        new SendSchedule().execute(String.valueOf(spTime.getSelectedItem()), etMed.getText().toString(), etPid.getText().toString(), etDid.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        btnViewSch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(WelcomeNurse.this, viewSchedule.class);
                startActivity(i);
            }
        });

        btnViewVitals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(WelcomeNurse.this, viewVitals.class);
                startActivity(i);
            }
        });
    }

    private void addListenerOnSpinnerItemSelection() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    class SendSchedule extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WelcomeNurse.this);
            pDialog.setMessage("Entering Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            String jsonString;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("time", args[0]));
            params.add(new BasicNameValuePair("id", args[2]));
            params.add(new BasicNameValuePair("med", args[1]));
            params.add(new BasicNameValuePair("doc", args[3]));
            params.add(new BasicNameValuePair("func", "putSchedule"));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(getString(R.string.url_patient_info_nurse), "POST", params);

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
        }
    }
}
