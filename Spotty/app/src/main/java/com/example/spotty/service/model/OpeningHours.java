package com.example.spotty.service.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * The type Opening hours.
 */
public class OpeningHours {
    /**
     * The Day.
     */
    @SerializedName("day")
    public int day;
    /**
     * The Opening Hours.
     */
    @SerializedName("hour")
    public String hours;

    /**
     * Instantiates a new Opening hours.
     *
     * @param day the day
     */
    public OpeningHours(int day) {
        this.day = day;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof OpeningHours && this.day == ((OpeningHours) obj).day;
    }
}
