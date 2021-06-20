package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.refuem.planbucket.Models.User;
import com.refuem.planbucket.Models.UsernameModel;
import com.refuem.planbucket.Services.NotifService;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameET,passwordET;
    private Button loginBTN;
    private final String loginURL = GlobalVariables.API_URL + "auth/login";
    private final LocalDataManager localDataManager = new LocalDataManager();
    
    private void bindViews(){
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
    }

    private boolean isEmpty(){
        if(usernameET.getText().toString().trim().equals("") || passwordET.getText().toString().trim().equals(""))
            return true;
        return false;
    }
    public void loginBTNOnClickHandler(View view){
        if(isEmpty()){
            Toast.makeText(this, "Kullanıcı adı/parola boş geçilemez!", Toast.LENGTH_SHORT).show();
        }else{
            User user = new User(usernameET.getText().toString(),passwordET.getText().toString());
            loginPageHTTPRequest(user);
        }
    }

    public void registerViewBTNOnClickHandler(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }

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
                                Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                                Intent notificationService = new Intent(LoginActivity.this, NotifService.class);
                                notificationService.putExtra("username",usernameModel.getUsername());
                                notificationService.putExtra("groupNames",groupNames);
                                notificationService.putExtra("token",usernameModel.getToken());
                                MyAccountManager accountManager = new MyAccountManager(LoginActivity.this,usernameModel.getUsername(),usernameModel.getToken(),notificationService);
                                intent.putExtra("accountManager",accountManager);
                                LoginActivity.this.startActivity(intent);
                                LoginActivity.this.finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void loginPageHTTPRequest(User user){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(user);
        try{
            JSONObject userJSON = new JSONObject(jsonString);
            JsonObjectRequest loginPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    loginURL,
                    userJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                getGroupsHTTPRequest(new UsernameModel(user.getUsername(),responseModel.getBody()));
                            }
                            else
                                Toast.makeText(LoginActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(LoginActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(loginPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}