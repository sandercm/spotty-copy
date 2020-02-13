package com.example.spotty.service.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

/**
 * The Location Transfer Object
 */
public class Location {
    /**
     * The Id of the location.
     */
    @SerializedName("id")
    public int id;
    /**
     * The Locationname.
     */
    @SerializedName("name")
    public String name;
    /**
     * The Latitude of the location.
     */
    @SerializedName("lat")
    public String latitude;
    /**
     * The Longitude of the location.
     */
    @SerializedName("long")
    public String longitude;
    /**
     * The Municipality of the location.
     */
    @SerializedName("municipality")
    public String municipality;
    /**
     * The Streetnumber of the location.
     */
    @SerializedName("number")
    public String number;
    /**
     * The Postcode of the location.
     */
    @SerializedName("postcode")
    public String postcode;
    /**
     * The Street of the location.
     */
    @SerializedName("street")
    public String street;

    /**
     * The Opening hours.
     */
    @SerializedName("opening_hours")
    public List<OpeningHours> opening_hours;

    @NonNull
    @Override
    public String toString() {
        //this string is just for debugging
        return "id: " + id + " name: " + name + " latitude: " + latitude + " longitude " + longitude +
                " municipality: " + municipality + " number: " + number + " postcode: " + postcode + " street: " + street;
    }
}