package com.example.android.responder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_login_info = "http://172.16.102.185/responder/getInfo.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PASS = "info";
    private static final String TAG_EMAIL = "email";

    public EditText inputEmail;
    public EditText inputPass;

    String em="";
    String pa="";

    // products JSONArray
    JSONArray pass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create button
        Button btnCreateProduct = (Button) findViewById(R.id.btnLogin);
        inputEmail = (EditText) findViewById(R.id.etEmail);
        inputPass = (EditText) findViewById(R.id.etPassword);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                em = inputEmail.getText().toString();
                pa = inputPass.getText().toString();
                // creating new product in background thread
                new LoadAllProducts().execute();
            }
        });
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Getting Info. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", em));
            params.add(new BasicNameValuePair("pass", pa));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_login_info, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Info: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Intent i = new Intent(Login.this, WelcomePatient.class);
                    startActivity(i);
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();


        }

    }
}
