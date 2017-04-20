package com.example.android.responder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.firebase.client.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public TextView tvToken;
    private BroadcastReceiver broadcastReceiver;
    private BeaconManager beaconManager;
    private Region region1, region2, region3, region4;
    long previousRequestAt;
    LocationCode myLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String macAddress = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
        //Log.d("macAddress", macAddress);
        macAddress = "ABCDEFGHIJ";

        Firebase.setAndroidContext(this);

        String username = macAddress.replace(':','.');
        FirebaseMessaging.getInstance().subscribeToTopic("user_"+username);

        Button btnPatient = (Button) findViewById(R.id.btnPatient);
        Button btnNurse = (Button) findViewById(R.id.btnNurse);
        Button btnDoctor = (Button) findViewById(R.id.btnDoctor);

        btnPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Patient", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
            }

        });

        btnNurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Nurse", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, WelcomeNurse.class);
                startActivity(i);
            }

        });

        btnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Doctor", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, WelcomeDoc.class);
                startActivity(i);
            }

        });

        previousRequestAt = 0;
        myLocator = new LocationCode();
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                //   if ((System.currentTimeMillis() - previousRequestAt) > 10000)  {
                previousRequestAt = System.currentTimeMillis();
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    String locationKey = String.format("%d", nearestBeacon.getMajor());
                    String places = myLocator.getLocationAt(locationKey);
                    Log.d("UF", "You are at " + places);

                }
                //}
                // }
            }
        });
        //Use UUID, major and minor values of your beacon as parameters. Values must be in hexadecimal
        region1 = new Region("ranged region", UUID.fromString("99671428-1698-1999-0942-201884467701"), 4369, 4369);
        region2 = new Region("ranged region", UUID.fromString("99671428-1698-1999-0942-201884467701"), 8738, 8738);
        region3 = new Region("ranged region", UUID.fromString("99671428-1698-1999-0942-201884467701"), 13107, 13107);
        region4 = new Region("ranged region", UUID.fromString("99671428-1698-1999-0942-201884467701"), 17476, 17476);

        tvToken = (TextView) findViewById(R.id.tvToken);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tvToken.setText(sharedPrefManager.getInstance(MainActivity.this).getToken());
            }
        };

        if(sharedPrefManager.getInstance(this).getToken() != null) {
            tvToken.setText(sharedPrefManager.getInstance(this).getToken());
            Log.d("notification", sharedPrefManager.getInstance(this).getToken());
        }


        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIdService.BROADCAST_TOKEN));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region1);
                beaconManager.startRanging(region2);
                beaconManager.startRanging(region3);
                beaconManager.startRanging(region4);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
