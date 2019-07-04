package com.example.musta.memorableplaces;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location liveLocation;
    final private int timeInterval = 1000;
    final private int distanceInterval = 10;
    final private int mapZoom = 10;
    private Intent intent;
    private ArrayList<String> addedPlaces;
    private ArrayList<LatLng> addedLatLngs;
    private Intent resultIntent;
    private boolean startup = true;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeInterval,
                        distanceInterval, locationListener);
                liveLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                activeMapCamera();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /*
                if(!startup) {
                    liveLocation = new Location(location);
                    mMap.clear();
                    LatLng liveLatLng = new LatLng(liveLocation.getLatitude(), liveLocation.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(liveLatLng).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLatLng, mapZoom));
                }
                startup = false;
                */
                //disabled until needed
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeInterval,
                    distanceInterval, locationListener);
            liveLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            toaster("Location service enabled", true);
            activeMapCamera();
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeInterval,
                        distanceInterval, locationListener);

                liveLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                activeMapCamera();
            }
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        intent = new Intent(getApplicationContext(), MainActivity.class);
        mMap = googleMap;
        addedPlaces = new ArrayList<String>();
        addedLatLngs = new ArrayList<LatLng>();
        intent = new Intent(getApplicationContext(), MainActivity.class);

        if(liveLocation != null) {

        // Add a marker in Sydney and move the camera
            activeMapCamera();
        } else {
            LatLng liveLatLng = new LatLng(0, 0);
            //mMap.addMarker(new MarkerOptions().position(liveLatLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLatLng, mapZoom));
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String s = "";
                try {
                    List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(address != null && address.size() > 0) {
                        if(address.get(0).getAddressLine(0) != null){
                            for(int i = 0; i <= address.get(0).getMaxAddressLineIndex(); i++){
                                s += address.get(0).getAddressLine(i);
                            }
                        }
                    } else {
                        s += Double.toString(latLng.latitude);
                        s += ", " + Double.toString(latLng.longitude);
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title(s));
                    addedPlaces.add(s);
                    addedLatLngs.add(latLng);
                    intent.putExtra("list", addedPlaces);
                    intent.putExtra("coordinates", addedLatLngs);
                    setResult(Activity.RESULT_OK, intent);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }


        //getApplicationContext() gets context of app
    }

    private void activeMapCamera(){

        if(mMap != null && liveLocation != null) {
            resultIntent = getIntent();
            double latitude = resultIntent.getDoubleExtra("latitude", -999);
            double longitude = resultIntent.getDoubleExtra("longitude", -999);
            if(latitude == -999) {
                toaster("no location selected", false);

                mMap.clear();
                LatLng liveLatLng = new LatLng(liveLocation.getLatitude(), liveLocation.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(liveLatLng).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLatLng, mapZoom));
            } else {
                mMap.clear();
                LatLng liveLatLng = new LatLng(latitude, longitude);
                //mMap.addMarker(new MarkerOptions().position(liveLatLng).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLatLng, mapZoom));
            }
        }


    }
}
