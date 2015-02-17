package com.example.spartan13.myapplication.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by spartan13 on 19. 1. 2015.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    private Recorder recorder;
    private boolean active;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude = 0;// latitude
    double longitude = 0; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 2 meter

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 5 seconds

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private TextView textView;

    public GPSTracker(Context context, TextView textView) {
        this.mContext = context;
        this.textView = textView;
        this.recorder = new Recorder();
        getLocation();
        this.active = true;
        this.showAll();

    }



    private void showAll(){
        textView.setText("POČET MĚŘENÍ : "+this.getRecorder().getCountOfMeassure()+"\n"
                        + "CELKOVÁ VZDÁLENOST : "+Math.round(this.getRecorder().getTotalDistance()*1000)/(double)1000+" m \n"
                        + "PRŮMĚRNÁ RYCHLOST : "+Math.round(this.getRecorder().getAverageSpeed()*1000)/(double)1000+" km/h \n"
        );

        String add = "";
        for (Location location : this.getRecorder().getLocations()){
            add += location.getTime()+" - "+location.getLongitude()+" - "+location.getLatitude()+" - "+location.getAltitude()+""+"\n";
        }

        textView.setText(textView.getText()+"\n"+add);

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                // if GPS Enabled get lat/long using GPS Services
                //if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.v("location ", "" + location);


                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                //}

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public void renew(){
        this.recorder = new Recorder();
        this.showAll();
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
        this.getLocation();
        if (this.active == true) {
            this.recorder.addLocation(location);
            this.showAll();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isGPSEnabled() {
        return isGPSEnabled;
    }
}