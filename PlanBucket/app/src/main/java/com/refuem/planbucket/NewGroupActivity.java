package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.refuem.planbucket.Models.AddGroupModel;
import com.refuem.planbucket.Models.GroupInfoModel;
import com.refuem.planbucket.Models.ResponseModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NewGroupActivity extends AppCompatActivity {

    private final String addGroupURL = GlobalVariables.API_URL + "group/addGroup";

    private EditText groupNameET;
    private MyAccountManager myAccountManager;

    private void bindViews(){
        groupNameET = findViewById(R.id.groupNameET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        bindViews();
        this.myAccountManager = getIntent().getParcelableExtra("accountManager");
    }

    public void newGroupBTNOnClickHandler(View view){
        if(groupNameET.getText().toString().trim().equals("")){
            Toast.makeText(this, "Lütfen grup ismi giriniz..", Toast.LENGTH_SHORT).show();
        }else{
            AddGroupModel addGroupModel = new AddGroupModel(groupNameET.getText().toString(),myAccountManager.getUsername(),myAccountManager.getToken());
            addGroupHTTPRequest(addGroupModel);
        }
    }

    private void addGroupHTTPRequest(AddGroupModel addGroupModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(addGroupModel);
        try{
            JSONObject addGroupJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    addGroupURL,
                    addGroupJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Intent intent = new Intent(NewGroupActivity.this,GroupsActivity.class);
                                intent.putExtra("accountManager",myAccountManager);
                                NewGroupActivity.this.startActivity(intent);
                                NewGroupActivity.this.finish();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(NewGroupActivity.this);
                            }
                            else
                                Toast.makeText(NewGroupActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(NewGroupActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}