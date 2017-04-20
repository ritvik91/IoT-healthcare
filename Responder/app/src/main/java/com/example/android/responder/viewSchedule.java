package com.example.android.responder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class viewSchedule extends AppCompatActivity {

    final Context context = this;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    EditText etPid;
    TextView tvDocId;
    String[] schStrArray = new String[4];
    JSONObject schJsonArray;
    JSONObject json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_get_patient_info, null);

        tvDocId = (TextView) findViewById(R.id.docId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        etPid = (EditText) promptsView.findViewById(R.id.etGetPid);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                new GetSchedule().execute(String.valueOf(etPid.getText().toString()));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    class GetSchedule extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(viewSchedule.this);
            pDialog.setMessage("Getting Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", args[0]));
            params.add(new BasicNameValuePair("func", "getSchedule"));

            Log.d("Info: ", params.toString());
            // getting JSON string from URL
            json = jParser.makeHttpRequest(getString(R.string.url_patient_info_nurse), "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Info: ", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            tvDocId = (TextView) findViewById(R.id.docId);
            try {
                tvDocId.setText(json.get("doc").toString());
            }  catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            ListView lvItems = (ListView) findViewById(R.id.lv_items);
            ExpandableAdapter adapter = getAdapter();

            lvItems.setAdapter(adapter);
            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ExpandableAdapter adapter = (ExpandableAdapter) parent.getAdapter();

                    Item item = (Item) adapter.getItem(position);
                    if(item != null){
                        if(item.isExpanded){
                            item.isExpanded = false;

                        }else{
                            item.isExpanded = true;
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
            });

            if(pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }

        }
    }

    private ExpandableAdapter getAdapter(){

        int arrayPointer = 0;

        List<Item> items = new ArrayList<>();
        try {
            schJsonArray = json.getJSONObject("sch");

            if (schJsonArray.getString("morning") != null)
            {
                Item item = new Item();
                item.title = "Morning";
                item.description = schJsonArray.getString("morning");
                item.isExpanded = false;

                items.add(item);
            }
            if (schJsonArray.getString("afternoon") != null)
            {
                Item item = new Item();
                item.title = "Afternoon";
                item.description = schJsonArray.getString("afternoon");
                item.isExpanded = false;

                items.add(item);
            }
            if (schJsonArray.getString("evening") != null)
            {
                Item item = new Item();
                item.title = "Evening";
                item.description = schJsonArray.getString("evening");
                item.isExpanded = false;

                items.add(item);
            }
            if (schJsonArray.getString("night") != null) {
                Item item = new Item();
                item.title = "Night";
                item.description = schJsonArray.getString("night");
                item.isExpanded = false;

                items.add(item);
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return new ExpandableAdapter(this, items);
    }
}
