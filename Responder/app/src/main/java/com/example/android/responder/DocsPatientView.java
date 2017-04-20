package com.example.android.responder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DocsPatientView extends AppCompatActivity {

    TextView reqPid;
    Button btnGetCurTemp, btnGetTempHist, btnGetCurO2, btnGetO2Hist, btnGetEkg;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs_patient_view);

        btnGetCurTemp = (Button) findViewById(R.id.btnGetCurTemp);
        btnGetTempHist = (Button) findViewById(R.id.btnGetTempHist);
        btnGetCurO2 = (Button) findViewById(R.id.btnGetCurO2);
        btnGetO2Hist = (Button) findViewById(R.id.btnGetO2Hist);
        btnGetEkg = (Button) findViewById(R.id.btnGetEkg);

        Intent i = getIntent();
        pid = i.getStringExtra("pidRequested");

        reqPid = (TextView) findViewById(R.id.reqPid);
        reqPid.setText(pid);

        btnGetCurTemp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(DocsPatientView.this, showCurTemp.class);
                i.putExtra("pid", pid);
                startActivity(i);
            }
        });

        btnGetTempHist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(DocsPatientView.this, showTempHist.class);
                i.putExtra("pid", pid);
                startActivity(i);
            }
        });

        btnGetCurO2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(DocsPatientView.this, showCurO2.class);
                i.putExtra("pid", pid);
                startActivity(i);
            }
        });

        btnGetO2Hist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(DocsPatientView.this, showTempHist.class);
                startActivity(i);
            }
        });

        btnGetEkg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                Intent i = new Intent(DocsPatientView.this, viewVitals.class);
                startActivity(i);
            }
        });

    }
}
