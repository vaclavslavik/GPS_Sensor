package com.example.spartan13.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.spartan13.myapplication.model.Recorder;
import com.example.spartan13.myapplication.model.RecorderSQLiteHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * Created by spartan13 on 11. 2. 2015.
 */
public class OneTrackActivity extends ActionBarActivity {

    public static String TRACK_ID = "track_id";
    private int id;
    private RecorderSQLiteHelper db;
    private Recorder recorder;
    private XYPlot plot;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_track_diary);


        this.id = getIntent().getIntExtra(TRACK_ID, -1);
        db = new RecorderSQLiteHelper(this);
        String strategy = SettingsActivity.getStrategy(this);
        try {
            this.recorder = db.getRecorder(this.id).getRecorderFactory(strategy);
            this.graphPaint();
            this.setUpMapIfNeeded();
        } catch (Exception e) {
            Toast.makeText(this, R.string.not_choose_strategy + strategy, Toast.LENGTH_LONG).show();
        }

    }

    private void graphPaint(){

        TextView trackName = (TextView) findViewById(R.id.track_name);
        trackName.setText(""+getString(R.string.distance)+ recorder.getTotalDistance() + getString(R.string.distance_entity)
                +"\n" + getString(R.string.average_speed)+ recorder.getAverageSpeed() + getString(R.string.average_speed_entity));

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // Create a couple arrays of y-values to plot:
        Number[] seriesAverageSpeedNumbers = new Number[2 * this.recorder.getLocations().size()];
        Number[] seriesActualAverageSpeedNumbers = new Number[2 * this.recorder.getLocations().size()];
        Number[] seriesMapsNumbers = new Number[2 * this.recorder.getLocations().size()];


        Location firstLocation = this.recorder.getLocations().get(0);
        Location lastLocation = firstLocation;
        int index = 0;
        double distance = 0;
        for (Location actualLocation : this.recorder.getLocations()) {
            double actualTime =  ((actualLocation.getTime() - firstLocation.getTime()))/1000;
            distance += actualLocation.distanceTo(lastLocation);
            double averageSpeed = 0;
            double actualAverageSpeed = 0;


            if (actualTime != 0){
                averageSpeed = distance / actualTime * 3.6;
            }

            if (actualLocation.getTime()-lastLocation.getTime() != 0){
                actualAverageSpeed = actualLocation.distanceTo(lastLocation) / (actualLocation.getTime()-lastLocation.getTime())*3600;
            }

            seriesAverageSpeedNumbers[2 * index] = actualTime;
            seriesAverageSpeedNumbers[2 * index + 1] = averageSpeed;

            seriesActualAverageSpeedNumbers[2*index] = actualTime;
            seriesActualAverageSpeedNumbers[2*index+1] = actualAverageSpeed;

            seriesMapsNumbers[2*index] = actualLocation.getLongitude();
            seriesMapsNumbers[2*index+1] = actualLocation.getLatitude();

            lastLocation = actualLocation;
            index++;
        }

        // Turn the above arrays into XYSeries':
        XYSeries seriesAverageSpeeds = new SimpleXYSeries(
                Arrays.asList(seriesAverageSpeedNumbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, // Y_VALS_ONLY means use the element index as the x value
                getString(R.string.graph_series_average_speed));                             // Set the display title of the series

        XYSeries seriesActualAverageSpeeds = new SimpleXYSeries(
                Arrays.asList(seriesActualAverageSpeedNumbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, // Y_VALS_ONLY means use the element index as the x value
                getString(R.string.graph_series_actual_speed));                             // Set the display title of the series

        // same as above
        //XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);

        // add a new series' to the xyplot:
        plot.addSeries(seriesAverageSpeeds, series1Format);
        plot.addSeries(seriesActualAverageSpeeds, series2Format);
        // same as above:
        //plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-45);

    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED);
        float zoomNumber = 7;
        if ((this.recorder != null) && (this.recorder.getLocations() != null)) {
            for (Location location : this.recorder.getLocations()) {
                polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            Location centroid = this.recorder.getCentroid();
            Double diagonalDistance = this.recorder.getDiagonalDistance();

            if (diagonalDistance == null){
                zoomNumber = 0;
            }else{
                // zoom 10 odpovídá cca 40 000

                diagonalDistance = diagonalDistance*100000;

                double baseMultiple = 320000 / diagonalDistance;
                double enlarge = Math.log(baseMultiple)/Math.log(2);
                zoomNumber = 7 + (int)enlarge;
            }

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(centroid.getLatitude(), centroid.getLongitude()));
            map.moveCamera(center);
            map.animateCamera(CameraUpdateFactory.zoomTo(zoomNumber));
        }else{
            Toast.makeText(this, ""+R.string.nothing_locations, Toast.LENGTH_LONG).show();
        }


        Polyline polyline = map.addPolyline(polylineOptions);

    }

}
