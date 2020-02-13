package com.example.spotty.service.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * The Day Location Crowdedness Transfer Object
 */
public class LocationDataDay {
    /**
     * The Hour of the dataPoint.
     */
    @SerializedName("hour")
    public String hour;
    /**
     * The Minute of the dataPoint.
     */
    @SerializedName("minute")
    public String minute;
    /**
     * The number of people People.
     */
    @SerializedName("people")
    public int people;

    /**
     * Instantiates a new Location data day, where only people can be set
     *
     * @param people the people
     */
    public LocationDataDay(int people) {
        this.people = people;
    }

    /**
     * Instantiates a new Location data day.
     */
    public LocationDataDay(){}

    @NonNull
    @Override
    public String toString() {
        //this string is just for debugging
        return "the number of people is: " + people;
    }

    /**
     * Is relevant now boolean.
     *
     * @return the boolean
     */
    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public boolean isRelevantNow() {
        Date now, compare;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh|mm");
            compare = simpleDateFormat.parse(String.format("%1$s|%2$s", this.hour, this.minute));
            now = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            long test = TimeUnit.MINUTES.convert(now.getTime() - compare.getTime(), TimeUnit.MINUTES);
            return (Math.abs(test) < 20);
        } catch (ParseException e) {
            return false;
        }
    }
}