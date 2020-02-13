package com.example.spotty.service.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * The Preferences Data Transfer Object
 */
public class LocalPreferences {
    /**
     * The variable to indicate if they want to sync with the server.
     */
    @SerializedName("sync_with_server")
    public boolean sync;
    /**
     * The variable to indicate if they want to have small animations
     */
    @SerializedName("animate")
    public boolean animate;
    /**
     * The City of the user.
     */
    @SerializedName("city")
    public String city;
    /**
     * The Passed used to check if the request passed
     */
    @SerializedName("passed")
    public boolean passed;

    @NonNull
    @Override
    public String toString() {
        //this string is just for debugging
        return "sync: " + sync + "|animate: " + animate;
    }
}
