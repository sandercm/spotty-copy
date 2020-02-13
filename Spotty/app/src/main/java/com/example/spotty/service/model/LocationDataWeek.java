package com.example.spotty.service.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * The Week Location Crowdedness Transfer Object
 */
public class LocationDataWeek {
    /**
     * The Day of the week.
     */
    @SerializedName("day")
    public String day;
    /**
     * The Hour of the day.
     */
    @SerializedName("hour")
    public int hour;
    /**
     * The Minute of the hour.
     */
    @SerializedName("minute")
    public int minute;
    /**
     * The number of People.
     */
    @SerializedName("people")
    public int people;

    @NonNull
    @Override
    public String toString() {
        //this string is just for debugging
        return "at: " + day + " " + hour + ":" + minute + " the number of people is: " + people;
    }
}