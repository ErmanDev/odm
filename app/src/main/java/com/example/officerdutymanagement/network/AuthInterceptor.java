package com.example.officerdutymanagement.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_TOKEN = "auth_token";
    
    private static Context appContext;

    public AuthInterceptor() {
    }

    public static void setAppContext(Context context) {
        appContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Get token from SharedPreferences if context is available
        String token = null;
        if (appContext != null) {
            SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            token = prefs.getString(KEY_TOKEN, null);
        }
        
        // If token exists and request doesn't already have Authorization header, add it
        if (token != null && originalRequest.header("Authorization") == null) {
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);
            return chain.proceed(requestBuilder.build());
        }
        
        return chain.proceed(originalRequest);
    }
    
    // Static helper methods for token management
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}

