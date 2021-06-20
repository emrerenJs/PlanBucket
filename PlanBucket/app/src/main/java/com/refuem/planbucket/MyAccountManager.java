package com.refuem.planbucket;

import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.refuem.planbucket.Configurations.GlobalVariables;
import com.refuem.planbucket.Configurations.LocalDataManager;
import com.refuem.planbucket.Models.GroupInfoModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UsernameModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MyAccountManager implements Parcelable {
    private Intent notificationService;
    private String username;
    private String token;
    public int firstAccess;

    public MyAccountManager(AppCompatActivity appCompatActivity,String username, String token, Intent notificationService){
        this.username = username;
        this.token = token;
        this.notificationService = notificationService;
        this.firstAccess = 1;
        final LocalDataManager localDataManager = new LocalDataManager();
        localDataManager.setSharedPreference(appCompatActivity,"token",token);
        localDataManager.setSharedPreference(appCompatActivity,"username",username);
    }

    public void logout(AppCompatActivity appCompatActivity){
        Intent intent = new Intent(appCompatActivity, LoginActivity.class);
        final LocalDataManager localDataManager = new LocalDataManager();
        localDataManager.removeSharedPreference(appCompatActivity,"token");
        localDataManager.removeSharedPreference(appCompatActivity,"username");
        this.stopNotificationService(appCompatActivity);
        appCompatActivity.startActivity(intent);
        appCompatActivity.finish();
    }

    public void startNotificationService(AppCompatActivity appCompatActivity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            appCompatActivity.startForegroundService(notificationService);
        }else{
            appCompatActivity.startService(notificationService);
        }
    }

    public void stopNotificationService(AppCompatActivity appCompatActivity){
        appCompatActivity.stopService(notificationService);
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public Intent getNotificationService() {
        return notificationService;
    }

    protected MyAccountManager(Parcel in) {
        notificationService = in.readParcelable(Intent.class.getClassLoader());
        username = in.readString();
        token = in.readString();
        firstAccess = in.readInt();
    }

    public static final Creator<MyAccountManager> CREATOR = new Creator<MyAccountManager>() {
        @Override
        public MyAccountManager createFromParcel(Parcel in) {
            return new MyAccountManager(in);
        }

        @Override
        public MyAccountManager[] newArray(int size) {
            return new MyAccountManager[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(notificationService, flags);
        dest.writeString(username);
        dest.writeString(token);
        dest.writeInt(firstAccess);
    }
}
