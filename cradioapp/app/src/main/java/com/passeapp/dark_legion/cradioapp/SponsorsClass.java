package com.passeapp.dark_legion.cradioapp;


public class SponsorsClass {

    private int _id;
    private Double lat;
    private Double lon;
    private String address;
    private String imageLink;
    private String title;

    public SponsorsClass(int _id, Double lat, Double lon, String address, String imageLink, String title) {
        this._id = _id;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.imageLink = imageLink;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof SponsorsClass)) return false;
        SponsorsClass o = (SponsorsClass) obj;
        return o.title == this.title;
    }
}
