package com.example.musta.memorableplaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> placeList;
    private ArrayAdapter<String> adapter;
    private ArrayList<LatLng> latLngs;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent dummy = getIntent();
//        toaster("Testing intent...", false);
//        if (dummy == null){
//            toaster("Intent not found", true);
//        }

        placeList = new ArrayList<String>();
        latLngs = new ArrayList<LatLng>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = sharedPreferences.edit();

        try {
            placeList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString
                    ("list", ObjectSerializer.serialize(new ArrayList<String>())));

            ArrayList<Double> latitudes = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString(
                    "latitudes", ObjectSerializer.serialize(new ArrayList<Double>())));

            ArrayList<Double> longitudes = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString(
                    "longitudes", ObjectSerializer.serialize(new ArrayList<Double>())));

            for(int i = 0; i < latitudes.size(); i++){
                latLngs.add(new LatLng(latitudes.get(i), longitudes.get(i)));
            }

            if(placeList.size() == latLngs.size() && placeList.size() == 0){
                toaster("Cannot load data", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(placeList.size() < 1) {
            placeList.add("Add a new place...");
        }

        ListView places = (ListView) findViewById(R.id.places);
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, placeList);
        places.setAdapter(adapter);

        places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                if(i == 0){
                    startActivityForResult(intent, 1);
                } else {
                    intent.putExtra("latitude", latLngs.get(i - 1).latitude);
                    intent.putExtra("longitude", latLngs.get(i - 1).longitude);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        toaster("List changed", false);
        if(1 == requestCode){
            if(Activity.RESULT_OK == resultCode){
                ArrayList<String> something = data.getStringArrayListExtra("list");
                ArrayList<LatLng> something2 = data.getParcelableArrayListExtra("coordinates");
                placeList.addAll(something);
                latLngs.addAll(something2);
                adapter.notifyDataSetChanged();




                try {
                    ArrayList<Double> latitudes = new ArrayList<Double>();
                    ArrayList<Double> longitudes = new ArrayList<Double>();

                    for(int i = 0; i < latLngs.size(); i++){
                        latitudes.add(latLngs.get(i).latitude);
                        longitudes.add(latLngs.get(i).longitude);
                    }

                    editor.putString("list",
                            ObjectSerializer.serialize(placeList));
                    editor.putString("latitudes", ObjectSerializer.serialize(latitudes));
                    editor.putString("longitudes", ObjectSerializer.serialize(longitudes));

                    editor.commit();
                    toaster("Saved", false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(Activity.RESULT_CANCELED == resultCode){

            }


        }

    }

    public void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
        //getApplicationContext() gets context of app
    }
}
