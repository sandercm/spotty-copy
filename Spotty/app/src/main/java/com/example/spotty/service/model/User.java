package com.example.spotty.service.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * The User Data Transfer Object
 */
public class User {
    /**
     * The Mail used to log in
     */
    @SerializedName("mail")
    public String mail;
    /**
     * The Token used to authenticate the user
     */
    @SerializedName("token")
    public String token;
    /**
     * The Passed used to check if the request passed
     */
    @SerializedName("passed")
    public boolean passed;
    /**
     * The Uid.
     */
    @SerializedName("uid")
    public String uid;
    /**
     * The Is friend, -1: a normal user, not a friend of any kind 0: request_sent, 1: approved, 2: to_approve
     */
    @SerializedName("isFriend")
    public int isFriend;
    /**
     * received friend request?
     */
    public boolean isrequest;

    /**
     * Instantiates a new User.
     *
     * @param mail      the mail
     * @param token     the token
     * @param isFriend  the isFriend
     * @param isrequest the isrequest
     */
    public User(String mail, String token, int isFriend, boolean isrequest) {
        this.mail = mail;
        this.token = token;
        this.isFriend = isFriend;
        this.isrequest = isrequest;
    }

    /**
     * Instantiates a new User.
     *
     * @param mail  the mail
     * @param token the token
     * @param uid   the uid
     */
    public User(String mail, String token, String uid) {
        this.mail = mail;
        this.token = token;
        this.uid = uid;
    }


    /**
     * Instantiates a new empty User.
     */
    public User() {
    }

    @NonNull
    @Override
    public String toString() {
        //this string is just for debugging
        return "username/mail: " + mail + " | token: " + token;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof User && ((User) obj).mail.equals(this.mail);
    }
}
