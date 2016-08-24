package com.jpilay.bueesclient.models;

/**
 * Created by jpilay on 21/08/16.
 */

public class DriverPublication {
    String id, date, description, hour, image;
    Boolean status;
    BusRoute busRoute;

    public DriverPublication(String id, String date, String description, String hour, String image, Boolean status, BusRoute busRoute) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.hour = hour;
        this.image = image;
        this.status = status;
        this.busRoute = busRoute;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public BusRoute getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute) {
        this.busRoute = busRoute;
    }
}
