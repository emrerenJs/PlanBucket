package com.refuem.planbucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.refuem.planbucket.Configurations.GlobalVariables;
import com.refuem.planbucket.Configurations.LocalDataManager;
import com.refuem.planbucket.Models.ProfileInfoModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UsernameModel;
import com.refuem.planbucket.Services.GPSTracker;
import com.refuem.planbucket.Services.NotifService;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView pUsernameTV, firstlastName,jobTV,biographyTV, locationTV;
    private ImageView profilePhotoPIC;
    private final String getProfileURL = GlobalVariables.API_URL + "profile/getProfile";
    private ProfileInfoModel profileInfoModel;
    private GPSTracker gps;

    /*SWIPE*/
    private SwipeRefreshLayout profileSwipeLayout;
    /*SWIPE*/

    private MyAccountManager myAccountManager;

    private void bindViews(){
        pUsernameTV = findViewById(R.id.pUsernameTV);
        firstlastName = findViewById(R.id.firstlastName);
        jobTV = findViewById(R.id.jobTV);
        biographyTV = findViewById(R.id.biographyTV);
        profilePhotoPIC = findViewById(R.id.profilePhotoPIC);
        locationTV = findViewById(R.id.locationTV);
        
        this.profileSwipeLayout = findViewById(R.id.profileSwipeLayout);
        this.profileSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ProfileActivity.this.getLocation();
                if(profileSwipeLayout.isRefreshing()){
                    profileSwipeLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bindViews();
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        if(myAccountManager.firstAccess == 1){
            myAccountManager.firstAccess = 0;
            myAccountManager.startNotificationService(this);
        }
        getLocation();
        getProfileHTTPRequest(new UsernameModel(myAccountManager.getUsername(),myAccountManager.getToken()));

    }

    public void goGroupsOnClickHandler(View v){
        Intent intent = new Intent(this,GroupsActivity.class);
        intent.putExtra("accountManager",myAccountManager);
        ProfileActivity.this.startActivity(intent);
        ProfileActivity.this.finish();
    }
    
    public void myInvitesBTNOnClickHandler(View v){
        Intent intent = new Intent(this,MyInvitesActivity.class);
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
    }
    public void changeBTNOnClickHandler(View v){
        Intent intent = new Intent(this,EditProfileActivity.class);
        intent.putExtra("profileInfo",profileInfoModel);
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
    }
    public void logoutBTNOnClickHandler(View v){
        myAccountManager.logout(this);
    }

    public void getLocation(){
        if(this.isGpsGranted()){
            gps = new GPSTracker(ProfileActivity.this);
            if(gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        locationTV.setText(addresses.get(0).getAdminArea() + "/" + addresses.get(0).getCountryName());
                    }
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                gps.showSettingsAlert();
            }
        }
    }
    
    public boolean isGpsGranted(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },0);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 0:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else{
                    Toast.makeText(this, "Lokasyon bilgisi alınamadı!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getProfileHTTPRequest(UsernameModel usernameModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(usernameModel);
        try{
            JSONObject usernameJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getProfileURL,
                    usernameJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                profileInfoModel = gson.fromJson(responseModel.getBody(),ProfileInfoModel.class);
                                pUsernameTV.setText(usernameModel.getUsername());
                                firstlastName.setText(profileInfoModel.getFirstname() + " " + profileInfoModel.getLastname());
                                jobTV.setText(profileInfoModel.getJob());
                                biographyTV.setText(profileInfoModel.getBiography());
                                if(profileInfoModel.getPhoto().equals("none")){
                                    profilePhotoPIC.setBackgroundResource(R.drawable.buksy);
                                }else{
                                    Picasso.with(ProfileActivity.this).invalidate(GlobalVariables.API_URL + profileInfoModel.getPhoto());
                                    Picasso.with(ProfileActivity.this)
                                            .load(GlobalVariables.API_URL + profileInfoModel.getPhoto())
                                            .placeholder(R.drawable.buksy)
                                            .fit()
                                            .centerInside()
                                            .into(profilePhotoPIC);
                                }
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(ProfileActivity.this);
                            }
                            else
                                Toast.makeText(ProfileActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(ProfileActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}