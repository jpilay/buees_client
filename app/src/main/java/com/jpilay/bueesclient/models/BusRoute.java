package com.jpilay.bueesclient.models;

/**
 * Created by jpilay on 21/08/16.
 */
public class BusRoute {
    String id, name;

    public BusRoute(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
