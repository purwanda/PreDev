package com.example.kanjeng.predev;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 * Created by kanjeng on 12/1/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    EditText mEvent;
    public TextView lokasi;
    public AlertDialog dialog;
    public Location currentLocation;
    LocationManager locationManager;
    double latitude,longitude;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapready : map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            currentLocation = getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        lokasi = (TextView) findViewById(R.id.textLokasi);

        getLocationPermission();
/*
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d(TAG, "onMapready : NETWORK_PROVIDER");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onMapready : NETWORK_PROVIDER,onLocationChanged");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude,longitude);
                    Log.d(TAG, "onMapready : NETWORK_PROVIDER,onLocationChanged lat:"+latitude+",long:"+longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<android.location.Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                        String str = addressList.get(0).getLocality()+" ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.2f));
                        lokasi.setText(latitude+","+longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            });
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d(TAG, "onMapready : GPS_PROVIDER");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onMapready : GPS_PROVIDER,onLocationChanged");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude,longitude);
                    Log.d(TAG, "onMapready : GPS_PROVIDER,onLocationChanged lat:"+latitude+",long:"+longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<android.location.Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                        String str = addressList.get(0).getLocality()+" ";
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.2f));
                        lokasi.setText(latitude+","+longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            });

        }
*/
    }

    private Location getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation : getting the device current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            currentLocation = (Location) task.getResult();
                            Log.d(TAG,"onComplete: found location! "+currentLocation.getLatitude()+","+currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            lokasi.setText(currentLocation.getLatitude()+","+currentLocation.getLongitude());
                        }else {
                            Log.d(TAG,"onComplete: current location is null");
                            Toast.makeText(MapActivity.this,"unable to get current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation : SecurityException: "+e.getMessage());
        }
        return currentLocation;
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG,"moveCamera : moving the camera to : lat: " + latLng.latitude + ", lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private void initMap(){
        Log.d(TAG,"initMap : initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission : getting locations permissions");
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult : called.");
        mLocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i=0;i<grantResults.length;i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult : permission failed");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult : permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    public void AddEvent(View view){
        Toast.makeText(this,currentLocation.getLatitude()+","+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        mEvent = (EditText)mView.findViewById(R.id.etEvent);
        Button mLogin = (Button)mView.findViewById(R.id.btnSend);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }

    public void sendEvent(View view){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        currentLocation=getDeviceLocation();

        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
//                .position(new LatLng(latitude, longitude))
                .title(mEvent.getText().toString());
        mMap.addMarker(options);
        Toast.makeText(this,currentLocation.getLatitude()+","+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }



}
