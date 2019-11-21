package com.example.smartpark;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingSlot implements Parcelable {
    private String status;
    private String latitude;
    private String longitude;
    private String address;
    private String parkingArea;

    public ParkingSlot(String status, String latitude, String longitude, String address, String parkingArea) {
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.parkingArea = parkingArea;
    }

    public ParkingSlot() {
    }

    protected ParkingSlot(Parcel in) {
        status = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        address = in.readString();
        parkingArea = in.readString();
    }

    public static final Creator<ParkingSlot> CREATOR = new Creator<ParkingSlot>() {
        @Override
        public ParkingSlot createFromParcel(Parcel in) {
            return new ParkingSlot(in);
        }

        @Override
        public ParkingSlot[] newArray(int size) {
            return new ParkingSlot[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(address);
        dest.writeString(parkingArea);
    }
}