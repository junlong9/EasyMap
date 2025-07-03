package com.example.easymap;

public class PlaceResult {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String distance;
    private String type;
    private boolean isPromoted;
    
    public PlaceResult(String name, String address, double latitude, double longitude, String distance, String type) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.type = type;
        this.isPromoted = false;
    }
    
    public PlaceResult(String title, String snippet, double latitude, double longitude, String address) {
        this.name = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = "未知距离";
        this.type = snippet;
        this.isPromoted = false;
    }
    
    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDistance() { return distance; }
    public String getType() { return type; }
    public boolean isPromoted() { return isPromoted; }
    
    // Additional getters for compatibility
    public String getTitle() { return name; }
    public String getSnippet() { return type; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDistance(String distance) { this.distance = distance; }
    public void setType(String type) { this.type = type; }
    public void setPromoted(boolean promoted) { isPromoted = promoted; }
} 