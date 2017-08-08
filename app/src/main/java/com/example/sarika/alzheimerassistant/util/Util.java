package com.example.sarika.alzheimerassistant.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.format.Formatter;

import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sarika on 3/18/17.
 */
public class Util {

    public static final String PREFERNCES = "Alzheimer's Assistant";
    public static final String TOKEN = "token";
    public static final String ROLE = "role";
    public static final String MSG_TO_SERVICE = "Sending Location Info";
    public static final String LOCATION_DESC="locationDescription";
    public static final String LOCATION_NAME="locationName";
    public static final String APPLICATION_JSON = "application/json";
    public static final String FIRST_TIME_LOGIN="firstTimeLogin";
    public static final String CAREGIVER = "caregiver";
    public static final String PATIENT = "patient";
    public static final String LOCATION_REMINDER_DETAIL_PARCEL_KEY = "locationReminderParcel";

    //Server Url
    public static String SERVER_URL = "https://alzheimers-server.herokuapp.com";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String FCM_TOKEN = "fcmToken";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static final String[] TOPICS = {"global"};

    public static String ipAddress;

    //Cache User Credentials

    public static final String USERID="userId";
    public static final String USERNAME="username";

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = serverUrl;
    }

    public static void setIpAddress(String ipAddress) {
        Util.ipAddress = ipAddress;
        setServerUrl("http://"+Util.ipAddress+":8080");
    }

    public static Map<String,String> getHeader(String token){

        Map<String,String> header = new HashMap<>();
        if(header!=null){

            header.put(HttpHeaders.AUTHORIZATION,token);
            header.put(HttpHeaders.CONTENT_TYPE,APPLICATION_JSON);

        }
        return header;
    }

    public static Map<String,String> getMultipartHeader(){
        String token  = "JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJfaWQiOiI1OGFlMTM1ZTAzMDU4NzIzMmNjNTE0OTciLCJlbWFpbCI6ImhpbWFuc2h1LmphaW4xMUBnbWFpbC5jb20" +
                "iLCJyb2xlIjoiY2FyZWdpdmVyIiwiaWF0IjoxNDkyMTExOTY2LCJleHAiOjE0OTI3MTY3NjZ9.Ya_XQeB1orIM8_" +
                "PLFA5fiIFN95TFb_CoJU3Mkyo7CMs";
        Map<String,String> header = new HashMap<>();
        if(header!=null){

            header.put(HttpHeaders.AUTHORIZATION,token);
            header.put(HttpHeaders.CONTENT_TYPE,"multipart/form-data");

        }
        return header;
    }

    public static Map<String,String> getHeader(){

        Map<String,String> header = new HashMap<>();
        if(header!=null){

            header.put(HttpHeaders.CONTENT_TYPE,APPLICATION_JSON);

        }
        return header;
    }
}
