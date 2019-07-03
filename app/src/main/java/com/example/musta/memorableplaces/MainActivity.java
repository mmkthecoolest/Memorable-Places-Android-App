package com.example.musta.memorableplaces;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> placeList;
    private ArrayAdapter<String> adapter;
    private ArrayList<LatLng> latLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent dummy = getIntent();
        toaster("Testing intent...", false);
        if (dummy == null){
            toaster("Intent not found", true);
        }

        placeList = new ArrayList<String>();
        latLngs = new ArrayList<LatLng>();
        placeList.add("Add a new place...");

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
        if(1 == requestCode){
            if(Activity.RESULT_OK == resultCode){
                ArrayList<String> something = data.getStringArrayListExtra("list");
                ArrayList<LatLng> something2 = data.getParcelableArrayListExtra("coordinates");
                placeList.addAll(something);
                latLngs.addAll(something2);
                adapter.notifyDataSetChanged();
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
