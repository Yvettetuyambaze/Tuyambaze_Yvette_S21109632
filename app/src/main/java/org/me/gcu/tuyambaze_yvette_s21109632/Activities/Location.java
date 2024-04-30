package org.me.gcu.tuyambaze_yvette_s21109632.Activities;

public class Location {
    private String name;
    private int id;

    public Location(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}