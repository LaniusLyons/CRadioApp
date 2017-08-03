package com.passeapp.dark_legion.cradioapp;


public class SponsorsClass {

    private int _id;
    private String name;
    private String link;
    private Double lat;
    private Double lon;
    private String address;
    private String imageLink;

    public SponsorsClass(int _id,String name, String link, Double lat, Double lon, String address, String imageLink) {
        this._id = _id;
        this.name = name.replace("/","").replace(".","");
        this.link = link;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.imageLink = imageLink;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.replace("/","").replace(".","");
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
        return o.getName() == this.getName();
    }
}
