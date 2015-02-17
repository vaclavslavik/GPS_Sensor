package com.example.spartan13.myapplication.model;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by spartan13 on 19. 1. 2015.
 */
public class Recorder {
    private ArrayList<Location> locations;
    private Location centroid;
    private Boolean calculateChanges;
    private Double diagonalDistance;
    private double distance;

    public Recorder() {
        this.locations = new ArrayList<>();
        this.calculateChanges = false;
        this.centroid = null;
        this.diagonalDistance = null;
        this.distance = 0;
    }

    public void addLocation(Location location) {
        this.locations.add(location);
        this.calculateChanges = true;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public int getCountOfMeassure() {
        return this.locations.size();
    }

    public double getTotalDistance() {
        this.calculateIfNeeds();
        return this.distance;
    }

    public double getAverageSpeed() {
        if (this.locations.size() >= 2) {
            return Math.abs(3.6 * this.getTotalDistance() / ((this.locations.get(0).getTime() - this.locations.get(this.locations.size() - 1).getTime()) / 1000));
        }
        return 0;
    }

    public Double getDiagonalDistance() {
        this.calculateIfNeeds();
        return diagonalDistance;
    }

    public Location getCentroid() {
        this.calculateIfNeeds();
        return centroid;
    }

    private void calculateIfNeeds(){
        if (this.calculateChanges == true) {


            // počítáná vzdálenosti
            double distance = 0;

            if (this.locations.size() >= 2) {
                for (int i = 1; i < this.locations.size(); i++) {
                    distance += this.locations.get(i).distanceTo(this.locations.get(i - 1));
                }
            }
            this.distance = distance;

            // počítání centroidu, uhlopříčky
            if (locations.size() < 1) {
                this.centroid = null;
                this.diagonalDistance = null;
            } else {
                double minLat = locations.get(0).getLatitude(), minLong = locations.get(0).getLongitude(), maxLat = locations.get(0).getLatitude(), maxLong = locations.get(0).getLongitude();
                for (Location location : this.getLocations()) {
                    if (location.getLatitude() < minLat) {
                        minLat = location.getLatitude();
                    }
                    if (location.getLatitude() > maxLat) {
                        maxLat = location.getLatitude();
                    }

                    if (location.getLongitude() < minLong) {
                        minLong = location.getLongitude();
                    }
                    if (location.getLongitude() > maxLong) {
                        maxLong = location.getLongitude();
                    }
                }
                Location centroid = new Location("NA");
                centroid.setLongitude((minLong + maxLong) / 2);
                centroid.setLatitude((minLat + maxLat) / 2);
                this.centroid = centroid;
                this.diagonalDistance = Math.sqrt(Math.pow(maxLong-minLong,2)+Math.pow(maxLat-minLat,2));
            }

            this.calculateChanges = false;
        }
    }

    public Recorder getRecorderFactory(String strategy) throws Exception {
        if (strategy.equals("DIRECT") || (strategy.equals(""))) {
            return this;
        }else if (strategy.equals("2POINT_AVERAGE")){
            return this.getAverageRecorder();
        }else if (strategy.equals("50M_AVERAGE")){
            return this.get50AverageRecorder();
        }

        else{
            throw  new Exception("UNDEFINED STRATEGY");
        }
    }

    private Recorder get50AverageRecorder(){
        Recorder recorderN = new Recorder();

        Location lastLocation = null;
        double distance = 0;
        ArrayList<Location> beforeLocations = new ArrayList<>();
        for (Location actualLocation : this.getLocations()){
            // projít vše, po 50+ m počítat průměr

            // při prvním průchodu nelze počítat vzdálenost od předešlého bodu
            if (lastLocation != null){
                distance += actualLocation.distanceTo(lastLocation);
                beforeLocations.add(actualLocation);

                if (distance > 50){ // pokud je vzdálenost větši, přidat bod, vynulovat aktuální hodnoty
                    distance = 0;
                    recorderN.addLocation(this.createAverageLocation(beforeLocations));
                    beforeLocations = new ArrayList<>();
                }

            }



            lastLocation = actualLocation;
        }


        return recorderN;
    }

    private Location createAverageLocation(ArrayList<Location> locations){
        Location location = new Location("NA");
        double latitude = 0;
        double longitude = 0;
        long time = 0;

        for (Location actualLocation : locations){
            time += actualLocation.getTime();
            latitude += actualLocation.getLatitude();
            longitude += actualLocation.getLongitude();
        }

        location.setLatitude(latitude/locations.size());
        location.setLongitude(longitude/locations.size());
        location.setTime(time/locations.size());

        return location;
    }

    private Recorder getAverageRecorder() {
        Recorder recorder1 = new Recorder();

        Location lastLocation = null;
        for (Location actualLocation : this.getLocations()) {

            if (lastLocation == null || (actualLocation.equals(this.getLocations().get(this.getLocations().size() - 1)))) {
                recorder1.addLocation(actualLocation);
            } else {
                Location newLocation = new Location("NA");
                newLocation.setAltitude((actualLocation.getAltitude() + lastLocation.getAltitude()) / 2);
                newLocation.setLatitude((actualLocation.getLatitude() + lastLocation.getLatitude()) / 2);
                newLocation.setLongitude((actualLocation.getLongitude() + lastLocation.getLongitude()) / 2);
                newLocation.setTime(new Long((actualLocation.getTime() + lastLocation.getTime()) / 2));
                recorder1.addLocation(newLocation);
            }


            lastLocation = actualLocation;
        }

        return recorder1;
    }

}
