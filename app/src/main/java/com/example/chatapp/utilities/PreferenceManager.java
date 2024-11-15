package com.example.chatapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;


    /**
     * Sets the preference of the user
     * @param context
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    /**
     * Determines if the user successfully logs in or already signed up
     * @param key
     * @param value
     */
    public void putBoolean(String key, Boolean value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key,value);

        editor.apply();

    }

    /**
     * Determines if a user is logged in or not
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {

        return sharedPreferences.getBoolean(key,false);



    }

    public void putString(String key, String value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key,value);

        editor.apply();

    }

    /**
     * Retrieves the value
     * @param key
     * @return
     */
    public String getString(String key) {

        return sharedPreferences.getString(key,null);

    }

    /**
     * Clears the sign in token
     */
    public void clear() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.apply();

    }

}
