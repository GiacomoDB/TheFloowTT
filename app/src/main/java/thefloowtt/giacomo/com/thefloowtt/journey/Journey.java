package thefloowtt.giacomo.com.thefloowtt.journey;

import java.util.Date;

/**
 * Created by giaco on 27/05/2017.
 */

public class Journey {
    private int id;
    private String startDate;
    private String endDate;
    private long duration;
    private float distance;
    private float averageSpeed;
    private double heightDifference;
    private String informations;
    private String locations;

    public Journey(){}

    public Journey(String startDate, String endDate, long duration, float distance, float averageSpeed, double heightDifference, String informations, String locations)
    {
        this.startDate 		    = startDate;
        this.endDate	        = endDate;
        this.duration	        = duration;
        this.distance	        = distance;
        this.averageSpeed 	    = averageSpeed;
        this.heightDifference	= heightDifference;
        this.informations       = informations;
        this.locations          = locations;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setHeightDifference(double heightDifference) {
        this.heightDifference = heightDifference;
    }

    public void setInformations(String informations) {
        this.informations = informations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public int getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public long getDuration() {
        return duration;
    }

    public float getDistance() {
        return distance;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public double getHeightDifference() {
        return heightDifference;
    }

    public String  getInformations() {
        return informations;
    }

    public String  getLocations() {
        return locations;
    }
}
