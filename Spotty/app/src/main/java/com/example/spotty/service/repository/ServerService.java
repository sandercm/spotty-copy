package com.example.spotty.service.repository;

import com.example.spotty.service.model.LocalPreferences;
import com.example.spotty.service.model.Location;
import com.example.spotty.service.model.LocationDataDay;
import com.example.spotty.service.model.LocationDataWeek;
import com.example.spotty.service.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * The interface Server service.
 */
public interface ServerService {

    /**
     * The constant HTTPS_LOCATION_API_URL.
     */
    String HTTPS_LOCATION_API_URL = "https://spotty.tech";

    /**
     * Gives all locations
     *
     * @return a {@link Call} to a {@link List} of {@link Location}
     */
    @GET("/location")
    Call<List<Location>> getLocations();

    /**
     * Gives all extra information about a location identified by an id
     *
     * @param id is the unique identifier for the location
     * @return a {@link Call} to a {@link List} of {@link Location}
     */
    @GET("/location/{id}")
    Call<List<Location>> getInfo(@Path("id") int id);

    /**
     * Gives the crowdedness data from a certain location of today
     *
     * @param id is the unique identifier for the location
     * @return a {@link Call} to a {@link List} of {@link LocationDataDay}
     */
    @GET("/location/{id}/day")
    Call<List<LocationDataDay>> getLocationDayData(@Path("id") int id);

    /**
     * Gives the crowdedness data from a certain location of last week
     *
     * @param id is the unique identifier for the location
     * @return a {@link Call} to a {@link List} of {@link LocationDataWeek}
     */
    @GET("/location/{id}/week")
    Call<List<LocationDataWeek>> getLocationWeekData(@Path("id") int id);

    /**
     * test if the server is reachable
     *
     * @return a {@link Void}
     */
    @HEAD("/")
    Call<Void> testNetwork();

    /**
     * check in a certain user at a certain location
     *
     * @param mail        is the mail/username of the user
     * @param token       is the token used to authenticate the user
     * @param uid         the uid used by the server to check authenticity
     * @param location_id is the identifier of the location
     * @return a {@link User} object where the "passed"-variable is most important
     */
    @FormUrlEncoded
    @POST("/user/checkin")
    Call<User> checkInUser(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid, @Field("location_id") int location_id);

    /**
     * check out a certain user
     *
     * @param mail  is the mail/username of the user
     * @param token is the token used to authenticate the user
     * @param uid   the uid used by the server to check authenticity
     * @return the call
     */
    @FormUrlEncoded
    @POST("/user/checkout")
    Call<User> checkOutUser(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid);

    /**
     * send the preferences of a certain user
     *
     * @param mail             is the mail/username of the user
     * @param token            is the token used to authenticate the user
     * @param uid              the uid used by the server to check authenticity
     * @param localPreferences is the new localPreferences from the user
     * @return the call
     */
    @FormUrlEncoded
    @POST("/user/preferences/set")
    Call<LocalPreferences> sendPreferences(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid, @Body LocalPreferences localPreferences);

    /**
     * get the preferences of a certain user
     *
     * @param mail  is the mail/username of the user
     * @param token is the token used to authenticate the user
     * @param uid   the uid used by the server to check authenticity
     * @return the preferences
     */
    @FormUrlEncoded
    @POST("/user/preferences/get")
    Call<LocalPreferences> getPreferences(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid);

    /**
     * tell the server we logged in
     *
     * @param mail  is the mail/username of the user
     * @param token is the token used to authenticate the user
     * @param uid   the uid used by the server to check authenticity
     * @return {@link User} with passed being most important
     */
    @FormUrlEncoded
    @POST("/user/uid")
    Call<User> sendLoginToServer(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid);

    /**
     * Gets all users.
     *
     * @return all {@link User}'s in a {@link List}
     */
    @GET("/user/all")
    Call<List<User>> getAllUsers();

    /**
     * get the friends of a user
     *
     * @param mail  is the mail/username of the user
     * @param token is the token used to authenticate the user
     * @param uid   the uid used by the server to check authenticity
     * @return a {@link List} of {@link User}
     */
    @FormUrlEncoded
    @POST("/friends/all")
    Call<List<User>> getFriendList(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid);

    /**
     * get the friends of a user at a certain location
     *
     * @param mail        is the mail/username of the user
     * @param token       is the token used to authenticate the user
     * @param uid         the uid used by the server to check authenticity
     * @param location_id is the identifier of the location
     * @return a {@link List} of {@link User}
     */
    @FormUrlEncoded
    @POST("/friends/location")
    Call<List<User>> getFriendListAtLocation(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid, @Field("location_id") int location_id);

    /**
     * send a friend request
     *
     * @param mail       is the mail/username of the user
     * @param token      is the token used to authenticate the user
     * @param uid        the uid used by the server to check authenticity
     * @param friendmail is the identifier of the soon to be friend
     * @return a {@link User}
     */
    @FormUrlEncoded
    @POST("/friends/request")
    Call<User> sendFriendRequest(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid, @Field("friendmail") String friendmail);

    /**
     * accept a friend request
     *
     * @param mail       is the mail/username of the user
     * @param token      is the token used to authenticate the user
     * @param uid        the uid used by the server to check authenticity
     * @param friendmail is the identifier of the soon to be friend
     * @return a {@link User}
     */
    @FormUrlEncoded
    @POST("/friends/approve")
    Call<User> acceptFriendRequest(@Field("mail") String mail, @Field("token") String token, @Field("uid") String uid, @Field("friendmail") String friendmail);
}
