package com.example.spotty.service.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.spotty.service.model.LocalPreferences;
import com.example.spotty.service.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The class for editing Local Preferences
 */
public class LocalPreferencesRepository {
    public static final String PREF_SETTINGS = "UserSettings";
    private static final String PREF_USER = "UserFile";
    private static final String ANIMATE_STRING = "animate";
    private static final String SYNC_STRING = "sync";
    private static final String IS_LOGGED_IN_STRING = "isLoggedIn";
    private static final String MAIL_STRING = "username";
    private static final String TOKEN_STRING = "token";
    private static final String LEVEL_STRING = "level";
    private static final String CITY_STRING = "city";
    private static final String UID_STRING = "uidtoken";
    private static final String PASSWORD_STRING = "password";
    private static final String SAVED_PASSWORD_STRING = "saved_password";
    private static LocalPreferencesRepository localPreferencesRepository;
    private ServerRepository serverRepository;

    private LocalPreferencesRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    /**
     * Gets instance.
     *
     * @param serverRepository the server repository
     * @return the instance
     */
    public static LocalPreferencesRepository getInstance(ServerRepository serverRepository) {
        if (localPreferencesRepository == null)
            localPreferencesRepository = new LocalPreferencesRepository(serverRepository);
        return localPreferencesRepository;
    }

    /**
     * update the local preferences with a new {@link LocalPreferences}-object
     *
     * @param context          the context in which it happens
     * @param localPreferences the new {@link LocalPreferences}
     */
    public void updateLocalPreferences(Context context, LocalPreferences localPreferences) {
        if (localPreferences != null && localPreferences.sync) {
            setAnimate(context, localPreferences.animate);
            setSync(context, localPreferences.sync);
            setUserLocation(context, localPreferences.city);
        }
    }

    /**
     * load the local preferences
     *
     * @param context the context in which it happens
     * @return the current {@link LocalPreferences}
     */
    public LocalPreferences getLocalPreferences(Context context) {
        LocalPreferences retVal = new LocalPreferences();
        retVal.sync = getSync(context);
        retVal.animate = getAnimate(context);
        retVal.city = getUserCity(context);
        return retVal;
    }

    /**
     * get the animate variable
     *
     * @param context the context in which it happens
     * @return the value of the animate variable
     */
    public Boolean getAnimate(Context context) {
        return context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).getBoolean(ANIMATE_STRING, false);
    }

    /**
     * update the animate variable
     *
     * @param context the context in which it happens
     * @param animate the new animate value
     */
    public void setAnimate(Context context, Boolean animate) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(ANIMATE_STRING, animate);
        editor.apply();
    }

    /**
     * get the sync variable
     *
     * @param context the context in which it happens
     * @return the value of the animate sync
     */
    public Boolean getSync(Context context) {
        return context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).getBoolean(SYNC_STRING, true);
    }

    /**
     * update the animate variable
     *
     * @param context the context in which it happens
     * @param sync    the new animate value
     */
    public void setSync(Context context, Boolean sync) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SYNC_STRING, sync);
        editor.apply();
    }

    /**
     * get the currently logged
     *
     * @return a {@link LiveData} from a {@link User}
     */
    public LiveData<User> getUser() {
        return serverRepository.getLoggedInUser();
    }

    /**
     * logout the current user
     *
     * @param context the context in which it happens
     */
    public void logOut(Context context) {
        serverRepository.logoutUser();
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_LOGGED_IN_STRING, false);
        editor.apply();
    }

    /**
     * get if the user is logged in or not
     *
     * @param context the context in which it happens
     * @return bool boolean
     */
    public boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getBoolean(IS_LOGGED_IN_STRING, false);
    }

    /**
     * same the mail and token from a user
     *
     * @param context the context in which it happens
     * @param user    the "new" user
     */
    public void initLongTermLogIn(Context context, User user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_LOGGED_IN_STRING, true);
        editor.putString(MAIL_STRING, user.mail);
        editor.putString(TOKEN_STRING, user.token);
        editor.putString(UID_STRING, user.uid);
        editor.apply();
    }

    /**
     * get the locally stored user
     *
     * @param context the context in which it happens
     * @return the stored user, will return null when no {@link User} is stored locally
     */
    public User getUser(Context context) {
        User retVal = null;
        boolean isloggedin = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getBoolean(IS_LOGGED_IN_STRING, false);
        if (isloggedin) {
            String mail = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getString(MAIL_STRING, "");
            String token = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getString(TOKEN_STRING, "");
            String uid = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getString(UID_STRING, "");
            if (mail != null && token != null && uid != null && !mail.equals("") && !token.equals("") && !uid.equals("")) {
                retVal = new User(mail, token, uid);
            }
        }
        return retVal;
    }

    /**
     * change the user its level
     *
     * @param context the context in which it happens
     * @param level   the new level
     */
    public void setUserLevel(Context context, String level) {
        // voor is admin!
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit();
        editor.putString(level, LEVEL_STRING);
        editor.apply();
    }

    /**
     * get the user its level
     *
     * @param context the context in which it happens
     * @return the user its level
     */
    public String getUserLevel(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getString(LEVEL_STRING, "");
    }

    /**
     * set the user its location
     *
     * @param context the context in which it happens
     * @param city    the new city
     */
    public void setUserLocation(Context context, String city) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit();
        editor.putString(city, CITY_STRING);
        editor.apply();
    }

    /**
     * get the user its location
     *
     * @param context the context in which it happens
     * @return the user its location
     */
    public String getUserCity(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getString(CITY_STRING, "");
    }

    /**
     * check if the user wants to save their password
     *
     * @param context       the context
     * @param passwordSaved the password saved
     */
    public void setUserPasswordSaved(Context context, boolean passwordSaved) {
        if (passwordSaved) {
            serverRepository.logoutUser();
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PASSWORD_STRING, passwordSaved);
        editor.apply();
    }

    /**
     * check if the user wants to save their password
     *
     * @param context the context
     * @return the user password saved
     */
    public boolean getUserPasswordSaved(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getBoolean(PASSWORD_STRING, false);
    }

    /**
     * Sets user saved password.
     *
     * @param context  the context
     * @param password the password
     */
    public void setUserSavedPassword(Context context, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).edit();
        editor.putString(SAVED_PASSWORD_STRING, password);
        editor.apply();
    }

    private String getUserSavedPassword(Context context) {
        return context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE).getString(SAVED_PASSWORD_STRING, "");
    }

    /**
     * Fix login, when token expired log in again
     *
     * @param context the context
     */
    public void fixLogin(Context context) {
        if (getUserPasswordSaved(context)) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            User user = getUser(context);
            mAuth.signInWithEmailAndPassword(user.mail, getUserSavedPassword(context)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser mUser;
                    mUser = mAuth.getCurrentUser();
                    mUser.getIdToken(false).addOnCompleteListener(token -> {
                        serverRepository.setLoggedInUser(new User(mUser.getEmail(), token.getResult().getToken(), mUser.getUid()), context);
                    });
                } else {
                    Log.d("firebase", "failure when trying to login ", task.getException());
                }
            });

        }
    }
}
