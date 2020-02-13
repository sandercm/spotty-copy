package com.example.spotty.service.repository;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotty.R;
import com.example.spotty.service.model.LocalPreferences;
import com.example.spotty.service.model.Location;
import com.example.spotty.service.model.LocationDataDay;
import com.example.spotty.service.model.LocationDataWeek;
import com.example.spotty.service.model.SpottyPicture;
import com.example.spotty.service.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class used for communication with the server
 */
public class ServerRepository {
    private static final String serverTag = "SERVERREPOSITORY";
    private static final String PICTURE_URL = ServerService.HTTPS_LOCATION_API_URL + "/locs/";
    private static ServerRepository serverRepository;
    private static long serverCheckDelay = 1000 * 60 * 5; //this is a delay in ms
    private ServerService serverService;
    private Resources resources;
    private MutableLiveData<Boolean> serverConnected;
    private Boolean serverConnectionTestRunning;
    private Handler handler;
    private Runnable handlerTask;
    private MutableLiveData<User> currentUser;
    private LocalPreferencesRepository localPreferencesRepository;
    private MutableLiveData<List<Location>> locationList;

    /**
     * create a new {@link ServerRepository}, requires no arguments
     */
    private ServerRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerService.HTTPS_LOCATION_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverService = retrofit.create(ServerService.class);
        serverConnected = new MutableLiveData<>();
        serverConnectionTestRunning = false;
        handler = new Handler();
        currentUser = new MutableLiveData<>();
        locationList = new MutableLiveData<>();
        if (localPreferencesRepository == null)
            localPreferencesRepository = LocalPreferencesRepository.getInstance(this);
    }

    /**
     * get a {@link ServerRepository}, this will use the internals to return a {@link ServerRepository}
     *
     * @return the new {@link ServerRepository}
     */
    public synchronized static ServerRepository getInstance() {
        // Create only 1 instance, if it is necessary
        if (serverRepository == null) {
            serverRepository = new ServerRepository();
        }
        return serverRepository;
    }

    /**
     * apply the rules for the search, like case-insensitivity
     *
     * @param str the input {@link String}
     * @return the output {@link String} with the applied rules
     */
    private static String applySearchRules(String str) {
        str = str.toLowerCase();
        str = str.replaceAll("\\s+", "");
        return str;
    }

    /**
     * trigger an update of the server connection check
     */
    public void triggerServerReachableUpdate() {
        serverService.testNetwork().enqueue(new LocalCallBack<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call, response);
                if (response.errorBody() != null) {
                    serverConnected.postValue(false);
                }
            }
        });
        Log.d(serverTag, "Triggered reachable update");
    }

    /**
     * get livedata element which tells if the server is connected
     *
     * @return livedata of a boolean which says if the server is connected
     */
    public LiveData<Boolean> serverReachableLive() {
        if (!serverConnectionTestRunning) {
            handlerTask = new Runnable() {
                @Override
                public void run() {
                    triggerServerReachableUpdate();
                    handler.postDelayed(this, serverCheckDelay);
                }
            };
            handler.post(handlerTask);
            serverConnectionTestRunning = true;
        }
        return serverConnected;
    }

    /**
     * stop the {@link ServerRepository} checking network connection
     */
    private void stopServerCheck() {
        handler.removeCallbacks(handlerTask);
        serverConnectionTestRunning = false;
    }

    /**
     * Trigger an update of the LocationList
     */
    private void triggerLocationListUpdate() {
        serverService.getLocations().enqueue(new LocalCallBack<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                super.onResponse(call, response);
                locationList.postValue(response.body());
            }
        });
    }

    /**
     * Get all the locations in a list
     *
     * @return List of Locations wrapped as a {@link LiveData} of a @{@link List}
     */
    public LiveData<List<Location>> getLocationList() {
        if (locationList.getValue() == null) {
            triggerLocationListUpdate();
            locationList.setValue(new ArrayList<>());
        }
        return locationList;
    }

    /**
     * Get all the locations in a list, filtered by a query
     *
     * @param searchStrings is the query
     * @return {@link List} of Locations
     */
    public List<Location> getFilteredLocationList(String searchStrings) {
        if (locationList.getValue() == null)
            triggerLocationListUpdate();
        List<Location> retVal = new ArrayList<>();
        List<Location> storedList = locationList.getValue();
        searchStrings = applySearchRules(searchStrings);
        if (storedList != null) {
            for (Location currentLocation : storedList) {
                String locationName = currentLocation.name;
                locationName = applySearchRules(locationName);
                if (locationName.contains(searchStrings)) {
                    retVal.add(currentLocation);
                }
            }
        }
        return retVal;
    }

    /**
     * Get more information about a location
     *
     * @param id is the unique identifier for the location
     * @return Location object wrapped in a {@link LiveData} object
     */
    public LiveData<Location> getLocationInfo(int id) {
        final MutableLiveData<Location> data = new MutableLiveData<>();
        data.setValue(new Location());
        serverService.getInfo(id).enqueue(new LocalCallBack<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                super.onResponse(call, response);
                List<Location> list = response.body();
                if (list == null || list.size() == 0) {
                    Log.e(serverTag, "Empty information list is not allowed for location with id " + id);
                } else {
                    data.postValue(list.get(0));
                }
            }
        });
        return data;
    }

    /**
     * Get crowdedness data from today from a certain location
     *
     * @param id is the unique identifier for the location
     * @return LocationDataDay object wrapped in a {@link LiveData} object
     */
    public LiveData<LocationDataDay> getLocationCrowdednessDay(int id) {
        MutableLiveData<LocationDataDay> data = new MutableLiveData<>();
        data.setValue(new LocationDataDay());
        serverService.getLocationDayData(id).enqueue(new LocalCallBack<List<LocationDataDay>>() {
            @Override
            public void onResponse(Call<List<LocationDataDay>> call, Response<List<LocationDataDay>> response) {
                super.onResponse(call, response);
                List<LocationDataDay> list = response.body();
                if (list == null || list.size() == 0) {
                    Log.e(serverTag, "Empty crowdedness list is not allowed for location with id " + id);
                } else {
                    int i = 0;
                    boolean stop = false;
                    while (i < list.size() && !stop) {
                        LocationDataDay elem = list.get(i);
                        if (elem.isRelevantNow()) {
                            data.postValue(elem);
                            stop = true;
                        }
                    }
                    if (!stop) {
                        new LocationDataDay(0);
                    }
                }
            }
        });
        return data;
    }

    /**
     * Get crowdedness data from today from a certain location
     *
     * @param id is the unique identifier for the location
     * @return LocationDataDay object wrapped in a {@link LiveData} object
     */
    public LiveData<List<LocationDataWeek>> getLocationCrowdednessWeek(int id) {
        MutableLiveData<List<LocationDataWeek>> data = new MutableLiveData<>();
        data.setValue(new ArrayList<>());
        serverService.getLocationWeekData(id).enqueue(new LocalCallBack<List<LocationDataWeek>>() {
            @Override
            public void onResponse(Call<List<LocationDataWeek>> call, Response<List<LocationDataWeek>> response) {
                super.onResponse(call, response);
                data.postValue(response.body());
            }
        });
        return data;
    }

    /**
     * Download the picture from the location
     *
     * @param id is the unique identifier for the location
     * @return LocationPicture object wrapped in a {@link LiveData} object
     */
    public LiveData<SpottyPicture> getLocationPicture(int id, Resources resources) {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.picture_not_found);
        return getLocationPictureOrDefault(id, defaultBitmap);
    }

    /**
     * Download the picture from the location, if not the default {@link Bitmap} is used
     *
     * @param id            is the unique identifier for the location
     * @param defaultBitmap the default bitmap
     * @return LocationPicture object wrapped in a {@link LiveData} object
     */
    public LiveData<SpottyPicture> getLocationPictureOrDefault(int id, Bitmap defaultBitmap) {
        return getPicture(PICTURE_URL + id + ".png", defaultBitmap);
    }

    /**
     * general function to download a {@link SpottyPicture}
     *
     * @param link the download-link
     * @return a {@link SpottyPicture} wrapped in a {@link LiveData} object
     */
    private LiveData<SpottyPicture> getPicture(String link, Bitmap defaultBitmap) {
        final MutableLiveData<SpottyPicture> data = new MutableLiveData<>();
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(link).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(serverTag, Log.getStackTraceString(e));
                serverConnected.postValue(false);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                ResponseBody body = response.body();
                Bitmap bitmap = null;
                if (body == null) {
                    Log.e(serverTag, "No body to get picture");
                } else {
                    InputStream inputStream = body.byteStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    // if the server has no picture, return the default picture
                }
                if (bitmap == null) {
                    bitmap = defaultBitmap;
                }
                SpottyPicture retVal = new SpottyPicture(bitmap);
                data.postValue(retVal);
                serverConnected.postValue(true);
            }
        });
        return data;
    }

    /**
     * set the logged in user, this will only work if no user is logged in
     *
     * @param user    the new user
     * @param context the context in which it happens
     * @return if the user is set to the new user, this will not be the case if a user was already logged in
     */
    public boolean setLoggedInUser(User user, Context context) {
        if (currentUser.getValue() == null) {
            serverService.sendLoginToServer(user.mail, user.token, user.uid).enqueue(new LocalCallBack<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    super.onResponse(call, response);
                    User body = response.body();
                    if (body != null && body.passed) {
                        currentUser.postValue(user);
                        localPreferencesRepository.initLongTermLogIn(context, user);
                    } else {
                        localPreferencesRepository.fixLogin(context);
                        Log.d(serverTag, "User failed to log in, server did not accept its credentials.");
                    }
                }
            });
            return true;
        }
        return false;
    }

    /**
     * give the current logged in {@link User}, null if no {@link User} is logged in
     *
     * @return {@link LiveData} which contains current logged in {@link User}
     */
    public LiveData<User> getLoggedInUser() {
        return currentUser;
    }

    /**
     * logs out the currently logged in user and don't do anything when no user is logged in
     */
    public void logoutUser() {
        currentUser.setValue(null);
    }

    /**
     * Get all users.
     *
     * @return {@link LiveData} which contains all {@link User}'s in a {@link List}
     */
    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> retVal = new MutableLiveData<>();
        retVal.postValue(new ArrayList<>());
        serverService.getAllUsers().enqueue(new LocalCallBack<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                super.onResponse(call, response);
                List<User> body = response.body();
                if (body != null) {
                    for (User user : body) {
                        user.isFriend = -1;
                    }
                    body.remove(currentUser.getValue());
                    retVal.setValue(body);
                }
            }
        });
        return retVal;
    }

    /**
     * get the friends of a certain user
     *
     * @return a {@link List} of {@link User} wrapped in a {@link LiveData} object
     */
    public LiveData<List<User>> getFriends() {
        MutableLiveData<List<User>> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.getFriendList(user.mail, user.token, user.uid).enqueue(new LocalCallBack<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    super.onResponse(call, response);
                    List<User> body = response.body();
                    if (body != null) {
                        retVal.setValue(body);
                    }
                }
            });
        }
        return retVal;
    }

    /**
     * get the friends of a certain user at a certain location
     *
     * @param location_id is the identifier of the certain location
     * @return a {@link List} of {@link User} wrapped in a {@link LiveData} object
     */
    public LiveData<List<User>> getFriendsAtLocation(int location_id) {
        MutableLiveData<List<User>> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.getFriendListAtLocation(user.mail, user.token, user.uid, location_id).enqueue(new LocalCallBack<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    super.onResponse(call, response);
                    List<User> body = response.body();
                    if (body != null) {
                        for (int i = body.size() - 1; i > 0; i--)
                            if (body.get(i).mail == null)
                                body.remove(i);
                        retVal.setValue(body);
                    }
                }
            });
        }
        return retVal;
    }


    /**
     * Send friend request
     *
     * @param friendmail the friend its mail
     * @return a {@link Boolean} to indicate if it passed, Null if no {@link User} is logged in
     */
    public LiveData<Boolean> sendFriendRequest(String friendmail) {
        MutableLiveData<Boolean> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.sendFriendRequest(user.mail, user.token, user.uid, friendmail).enqueue(new LocalCallBack<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    super.onResponse(call, response);
                    retVal.postValue(true);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    super.onFailure(call, t);
                    retVal.postValue(false);
                }
            });
        }
        return retVal;
    }

    /**
     * Send friend request
     *
     * @param friendmail the friend its mail
     * @return a {@link Boolean} to indicate if it passed, Null if no {@link User} is logged in
     */
    public LiveData<Boolean> acceptFriendRequest(String friendmail) {
        MutableLiveData<Boolean> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.acceptFriendRequest(user.mail, user.token, user.uid, friendmail).enqueue(new LocalCallBack<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    super.onResponse(call, response);
                    retVal.postValue(true);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    super.onFailure(call, t);
                    retVal.postValue(false);
                }
            });
        }
        return retVal;
    }

    /**
     * check a user in at a certain location
     *
     * @param location_id is the identifier of the certain location
     * @return a {@link Boolean} wrapped in a {@link LiveData} object
     */
    public LiveData<Boolean> checkInUser(int location_id) {
        MutableLiveData<Boolean> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.checkInUser(user.mail, user.token, user.uid, location_id).enqueue(new LocalCallBack<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    super.onResponse(call, response);
                    User body = response.body();
                    if (body == null) {
                        retVal.postValue(false);
                    } else {
                        retVal.setValue(body.passed);
                    }
                }
            });
        }
        return retVal;
    }

    /**
     * check a user in at a certain location
     *
     * @return a {@link Boolean} wrapped in a {@link LiveData} object
     */
    public LiveData<Boolean> checkOutUser() {
        MutableLiveData<Boolean> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.checkOutUser(user.mail, user.token, user.uid).enqueue(new LocalCallBack<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    super.onResponse(call, response);
                    User body = response.body();
                    if (body == null) {
                        retVal.postValue(false);
                    } else {
                        retVal.setValue(body.passed);
                    }
                }
            });
        }
        return retVal;
    }

    /**
     * update the server preferences
     *
     * @param localPreferences {@link LocalPreferences} the new preferences
     * @return a {@link Boolean} wrapped in {@link LiveData}
     */
    public LiveData<Boolean> sendLocalPreferences(LocalPreferences localPreferences) {
        MutableLiveData<Boolean> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.sendPreferences(user.mail, user.token, user.uid, localPreferences).enqueue(new LocalCallBack<LocalPreferences>() {
                @Override
                public void onResponse(Call<LocalPreferences> call, Response<LocalPreferences> response) {
                    super.onResponse(call, response);
                    LocalPreferences body = response.body();
                    if (body == null) {
                        retVal.postValue(false);
                    } else {
                        retVal.setValue(body.passed);
                    }
                }
            });
        }
        return retVal;
    }

    /**
     * get the local preferences from the {@link User}
     *
     * @param context the context
     * @return a {@link LocalPreferences} wrapped in {@link LiveData} object
     */
    public LiveData<LocalPreferences> getLocalPreferences(Context context) {
        MutableLiveData<LocalPreferences> retVal = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            serverService.getPreferences(user.mail, user.token, user.uid).enqueue(new LocalCallBack<LocalPreferences>() {
                @Override
                public void onResponse(Call<LocalPreferences> call, Response<LocalPreferences> response) {
                    super.onResponse(call, response);
                    retVal.postValue(response.body());
                    localPreferencesRepository.updateLocalPreferences(context, response.body());
                }
            });
        }
        return retVal;
    }

    /**
     * new locally used {@link Callback}
     *
     * @param <T> the type parameter
     */
    class LocalCallBack<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            serverConnected.postValue(true);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e(serverTag, Log.getStackTraceString(t));
            // Only if the error is something network related, thu IOExecption, say the server is unreachable
            if (t instanceof IOException) {
                serverConnected.postValue(false);
            }
        }
    }
}
