package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.SendInviteModel;
import com.refuem.planbucket.Models.UserRoleModel;

import org.json.JSONObject;

public class InviteActivity extends AppCompatActivity {

    private EditText getProfileET;
    private final String inviteUserURL = GlobalVariables.API_URL + "group/inviteUser";
    private MyAccountManager myAccountManager;

    private void bindViews(){
        getProfileET = findViewById(R.id.getProfileET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        bindViews();
        this.myAccountManager = getIntent().getParcelableExtra("accountManager");
    }

    public void sendInviteBTNOnClickHandler(View v){
        if(getProfileET.getText().toString().trim().equals(""))
            Toast.makeText(this, "Lütfen kullanıcı adını boş bırakmayın..", Toast.LENGTH_SHORT).show();
        else{
            sendInviteHTTPRequest(new SendInviteModel(getIntent().getStringExtra("groupTitle"),getProfileET.getText().toString(),myAccountManager.getToken()));
        }
    }

    public void sendInviteHTTPRequest(SendInviteModel sendInviteModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(sendInviteModel);
        try{
            JSONObject sendInviteJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    inviteUserURL,
                    sendInviteJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Toast.makeText(InviteActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                                InviteActivity.this.finish();
                                InviteActivity.this.onBackPressed();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(InviteActivity.this);
                            }
                            else
                                Toast.makeText(InviteActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(InviteActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}