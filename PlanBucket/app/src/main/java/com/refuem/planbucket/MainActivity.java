package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.refuem.planbucket.Services.NotifService;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final LocalDataManager localDataManager = new LocalDataManager();

    private void getGroupsHTTPRequest(UsernameModel usernameModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(usernameModel);
        try{
            JSONObject usernameJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    GlobalVariables.API_URL + "group/myGroups",
                    usernameJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<GroupInfoModel>>(){}.getType();
                                ArrayList<GroupInfoModel> groupInfoModelList = gson.fromJson(responseModel.getBody(),(listType));
                                String[] groupNames = new String[groupInfoModelList.size()];
                                int i = 0;
                                for(GroupInfoModel groupInfoModel : groupInfoModelList){
                                    groupNames[i] = groupInfoModel.getTitle();
                                    i++;
                                }
                                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                                Intent notificationService = new Intent(MainActivity.this, NotifService.class);
                                notificationService.putExtra("username",usernameModel.getUsername());
                                notificationService.putExtra("token",usernameModel.getToken());
                                notificationService.putExtra("groupNames",groupNames);
                                MyAccountManager accountManager = new MyAccountManager(MainActivity.this,usernameModel.getUsername(),usernameModel.getToken(),notificationService);
                                intent.putExtra("accountManager",accountManager);
                                MainActivity.this.startActivity(intent);
                                MainActivity.this.finish();
                            }else{
                                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                MainActivity.this.startActivity(intent);
                                MainActivity.this.finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splashscreen_animation);
        logoImageView.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String token = localDataManager.getSharedPreference(MainActivity.this.getApplicationContext(),"token","");
                if(token.equals("")){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }else{
                    String username = localDataManager.getSharedPreference(getApplicationContext(),"username","");
                    getGroupsHTTPRequest(new UsernameModel(username,token));
                }
            }
        },3000);
    }
}