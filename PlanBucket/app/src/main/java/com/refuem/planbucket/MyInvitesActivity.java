package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.refuem.planbucket.Models.GroupTitleModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UsernameModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.acl.Group;
import java.util.ArrayList;

public class MyInvitesActivity extends AppCompatActivity {

    private final String getMyInvitesURL = GlobalVariables.API_URL + "group/getMyInvites";
    private final String acceptInviteURL = GlobalVariables.API_URL + "group/acceptInvite";
    private final String denyInviteURL = GlobalVariables.API_URL + "group/denyInvite";
    private MyAccountManager myAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invites);
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        getMyInvitesHTTPRequest(new UsernameModel(myAccountManager.getUsername(),myAccountManager.getToken()));
    }

    private void fillLayoutWithInvites(ArrayList<GroupTitleModel> inviteList){

        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams1);
        LinearLayout linearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);

        LinearLayout myLinearLayout = findViewById(R.id.myInvitesLayout);
        myLinearLayout.setGravity(Gravity.CENTER);

        ArrayList<LinearLayout> components = new ArrayList<>();
        for(GroupTitleModel invite : inviteList){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,250);
            final LinearLayout component = new LinearLayout(this);
            layoutParams.setMargins(100,150,100,0);
            component.setBackground(getDrawable(R.drawable.box));
            component.setLayoutParams(layoutParams);
            component.setOrientation(LinearLayout.HORIZONTAL);
            component.setPadding(0,0,40,0);

            final TextView textView = new TextView(this);
            textView.setText(invite.getGroupTitle());
            final LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsTextView.weight = 1;
            textView.setLayoutParams(layoutParamsTextView);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(25);
            textView.setTypeface(null, Typeface.BOLD);
            component.addView(textView,0);

            final Button buttonAccept = new Button(this);
            final LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsButton.weight = 2;
            buttonAccept.setLayoutParams(layoutParamsButton);
            buttonAccept.setText("Katıl");
            buttonAccept.setBackgroundColor(Color.TRANSPARENT);
            buttonAccept.setTextColor(Color.WHITE);
            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptInviteHTTPRequest(new AddGroupModel(invite.getGroupTitle(),myAccountManager.getUsername(),myAccountManager.getToken()));
                }
            });
            component.addView(buttonAccept,1);

            final Button buttonDeny = new Button(this);
            buttonDeny.setLayoutParams(layoutParamsButton);
            buttonDeny.setText("Reddet");
            buttonDeny.setBackgroundColor(Color.TRANSPARENT);
            buttonDeny.setTextColor(Color.WHITE);
            buttonDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    denyInviteHTTPRequest(new AddGroupModel(invite.getGroupTitle(),myAccountManager.getUsername(),myAccountManager.getToken()));
                }
            });
            component.addView(buttonDeny,2);

            linearLayout.addView(component);
            components.add(component);
        }
        if(myLinearLayout != null){
            myLinearLayout.addView(scrollView);
        }
    }

    private void getMyInvitesHTTPRequest(UsernameModel usernameModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(usernameModel);
        try{
            JSONObject usernameJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getMyInvitesURL,
                    usernameJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<GroupTitleModel>>(){}.getType();
                                ArrayList<GroupTitleModel> groupInfoModelList = gson.fromJson(responseModel.getBody(),(listType));
                                fillLayoutWithInvites(groupInfoModelList);
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MyInvitesActivity.this);
                            }
                            else
                                Toast.makeText(MyInvitesActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MyInvitesActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void acceptInviteHTTPRequest(AddGroupModel acceptInviteModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(acceptInviteModel);
        try{
            JSONObject usernameJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    acceptInviteURL,
                    usernameJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Toast.makeText(MyInvitesActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                                MyInvitesActivity.this.onBackPressed();
                                MyInvitesActivity.this.finish();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MyInvitesActivity.this);
                            }
                            else
                                Toast.makeText(MyInvitesActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MyInvitesActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void denyInviteHTTPRequest(AddGroupModel denyInviteModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(denyInviteModel);
        try{
            JSONObject usernameJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    denyInviteURL,
                    usernameJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Toast.makeText(MyInvitesActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                                MyInvitesActivity.this.onBackPressed();
                                MyInvitesActivity.this.finish();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MyInvitesActivity.this);
                            }
                            else
                                Toast.makeText(MyInvitesActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MyInvitesActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}