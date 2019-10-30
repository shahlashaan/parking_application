package com.example.smartpark;

public class ParkingSlot {
    private String status;
    private String longitude;
    private String latitude;
    private String address;
    private String parkingArea;

    public ParkingSlot(String status, String longitude, String latitude, String address, String parkingArea) {
        this.status = status;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.parkingArea = parkingArea;
    }

    public ParkingSlot() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(String parkingArea) {
        this.parkingArea = parkingArea;
    }


}
