package com.example.sarika.alzheimerassistant.other;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sarika.alzheimerassistant.util.Util;

/**
 * Created by sarika on 4/3/17.
 */
public class PreferenceManager {

    private static SharedPreferences sharedPreferences;

    public static String getTokenFromsharedPreference(Context context){

        sharedPreferences = getSharedPreference(context);
        String token = sharedPreferences.getString(Util.TOKEN,"");
        return token;
    }
    public static void deleteTokenFromSharedPreference(Context context){

        sharedPreferences = getSharedPreference(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Util.TOKEN);
        editor.remove(Util.ROLE);
        editor.remove(Util.FCM_TOKEN);
        editor.remove(Util.FIRST_TIME_LOGIN);
        editor.apply();
    }
    public static SharedPreferences getSharedPreference(Context context){
        return context.getSharedPreferences(Util.PREFERNCES,context.MODE_PRIVATE);
    }

    public static void setTokenToSharedPreference(Context context, String token){

        sharedPreferences = getSharedPreference(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(Util.TOKEN,token);
        editor.commit();
    }

    public static void setRoleToSharedPreference(Context context, String role){

        sharedPreferences = getSharedPreference(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(Util.ROLE,role);
        editor.commit();
    }

    public static String getRoleFromsharedPreference(Context context){

        sharedPreferences = getSharedPreference(context);
        String token = sharedPreferences.getString(Util.ROLE,"");
        return token;
    }

    public static void storeRegIdInPref(String token, Context context) {
        SharedPreferences pref = getSharedPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Util.FCM_TOKEN, token);
        editor.commit();
    }

    public static void setFirstTimeLogin(Context context, boolean flag){
        SharedPreferences pref = getSharedPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Util.FIRST_TIME_LOGIN, flag);
        editor.commit();
    }
    public static boolean getFirstTimeLogin(Context context){
        sharedPreferences = getSharedPreference(context);
        boolean firstTimeLogin = sharedPreferences.getBoolean(Util.FIRST_TIME_LOGIN,false);
        return firstTimeLogin;
    }

    public static void setUserDetails(Context context, String userId,String userName) {
        sharedPreferences=getSharedPreference(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Util.USERID,userId);
        editor.putString(Util.USERNAME,userName);
        editor.commit();
    }


    public static String getUserId(Context context){
        sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getString(Util.USERID,"");
    }

    public static String getUserName(Context context){
        sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getString(Util.USERNAME,"");
    }

}
