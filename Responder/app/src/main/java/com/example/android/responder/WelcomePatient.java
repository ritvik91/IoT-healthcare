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

import com.firebase.client.Firebase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.R.id.message;

public class WelcomePatient extends AppCompatActivity {

    private static final String KEY_FCM_TEXT = "keyFcmText";
    private static final String KEY_FCM_SENDER_ID = "keyFcmSenderId";
    private static final String FIREBASE_URL = "https://responder-4dc7a.firebaseio.com/";
    private static String url_get_location = "http://172.16.102.185/responder/getNearestMac.php";
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // url to get all products list
    private static String url_login_info = "http://172.16.102.185/responder/getInfo.php";
    private String nurseMacAddress="";

    public void setNurseMacAddress(String ma)
    {
        nurseMacAddress = ma;
    }

    public String getNurseMacAddress()
    {
        return nurseMacAddress;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_patient);

        Button btnTh = (Button) findViewById(R.id.btnTH);
        btnTh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new ConnectServer().execute();
            }
        });

        Button btnCan = (Button) findViewById(R.id.btnCAN);
        btnCan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new getNearestMac().execute();
                //sendNotificationToUser(getNurseMacAddress().replace(':','.'), "message2");
                new SendNotification().execute();
            }
        });
    }

    public static void sendNotificationToUser(String user, final String message) {
        Firebase ref = new Firebase(FIREBASE_URL);
        final Firebase notifications = ref.child("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);

        notifications.push().setValue(notification);
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class ConnectServer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WelcomePatient.this);
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
            params.add(new BasicNameValuePair("id", "123456"));
            params.add(new BasicNameValuePair("query", "1"));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_login_info, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Info: ", json.toString());

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

    class getNearestMac extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WelcomePatient.this);
            pDialog.setMessage("Getting a nurse. Please wait...");
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
            params.add(new BasicNameValuePair("area", "Marston"));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_get_location, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Info: ", json.toString());
            try {
                setNurseMacAddress(json.getString("message"));
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

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class SendNotification extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WelcomePatient.this);
            pDialog.setMessage("Calling a nurse for you. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            //send Push Notification
            String sender="124187186048";
            String receiverId=getNurseMacAddress().replace(':','.');

            HttpsURLConnection connection = null;
            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                //Put below you FCM API Key instead
                connection.setRequestProperty("Authorization", "key="
                        + "AAAAHOoiE4A:APA91bFf8ddfyA1XK6KxSp54p0SH3OG7svRGpAyQxjpP_Y-qkGDZvO-5uIc4r7C6vRqyL2_Wz-JAhKF4ooAFld5CN7JBRlLEfi4PwFYsYOgMOyrzR6wc85UcQ16dUf-cUoDzIqyGBoXr");

                JSONObject root = new JSONObject();
                JSONObject data = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("body", "new notification");
                notification.put("title", "hello world");
                data.put(KEY_FCM_TEXT, "Hello");
                data.put(KEY_FCM_SENDER_ID, sender);
                root.put("data", data);
                root.put("notification", notification);
                root.put("to", "/topics/user_" + receiverId);
                Log.d("receiver", receiverId);

                byte[] outputBytes = root.toString().getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(outputBytes);
                os.flush();
                os.close();
                connection.getInputStream(); //do not remove this line. request will not work without it gg

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
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
