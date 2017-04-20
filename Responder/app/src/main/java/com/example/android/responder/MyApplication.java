package com.example.android.responder;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by GrayShadow on 4/10/17.
 */

public class MyApplication extends Application {
    LocationCode myLocator;
    String currentLocation;
    private BeaconManager beaconManager;
    VolleyQueue volleyQueue;
    private ProgressDialog pDialog;
    String macAddress;
    JSONParser jParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static String url_insert_location = "http://172.16.102.185/responder/insert_location.php";
    private static String url_delete_location = "http://172.16.102.185/responder/delete_location.php";

    @Override
    public void onCreate() {
        super.onCreate();
        //volleyQueue = new VolleyQueue(getApplicationContext());
        myLocator = new LocationCode();
        macAddress = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                String location = myLocator.getLocationAt(String.valueOf(list.get(0).getMajor()));
                currentLocation = location;
                showNotification("Welcome", "You are at " + location);
                //volleyQueue.addMessage(new String[] {String.valueOf(myLocator.getCode(String.valueOf(list.get(0).getMajor()), "entry"))});

                //HTTPPostClient myRequestPostClient = new HTTPPostClient();
                //myRequestPostClient.execute(new String[]{location, macAddress, "Enter"});
                new InsertLocation().execute();
            }
            @Override
            public void onExitedRegion(Region region) {
                String location = myLocator.getLocationAt(String.valueOf(region.getMajor()));
                int locationCode = myLocator.getCode(String.valueOf(region.getMajor()), "exit");
                showNotification("Bye", "You left " + location);
                new DeleteLocation().execute();
                //volleyQueue.addMessage(new String[]{String.valueOf(locationCode)});
                //HTTPPostClient myRequestPostClient = new HTTPPostClient();
                // myRequestPostClient.execute(new String[]{location, macAddress, "Exit"});
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                //Use UUID, major and minor values of your beacon as parameters. Values must be in hexadecimal
                beaconManager.startMonitoring(new Region("monitored region 1",
                        UUID.fromString("99671428-1698-1999-0942-201884467701"), 4369, 4369));
                beaconManager.startMonitoring(new Region("monitored region 2",
                        UUID.fromString("99671428-1698-1999-0942-201884467701"), 8738 , 8738));
                beaconManager.startMonitoring(new Region("monitored region 3",
                        UUID.fromString("99671428-1698-1999-0942-201884467701"), 13107 , 13107));
                beaconManager.startMonitoring(new Region("monitored region 4",
                        UUID.fromString("99671428-1698-1999-0942-201884467701"), 17476 , 17476));
            }
        });
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    class InsertLocation extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MyApplication.this);
//            pDialog.setMessage("Inserting. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("area", currentLocation));
            params.add(new BasicNameValuePair("mac", macAddress));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_insert_location, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Info: ", json.toString());

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();
        }
    }

    class DeleteLocation extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MyApplication.this);
//            pDialog.setMessage("Deleting. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mac", macAddress));
            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_delete_location, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Info: ", json.toString());

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //pDialog.dismiss();
        }
    }
}

