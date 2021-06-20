package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.refuem.planbucket.Models.GroupTitleModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UserRoleModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MembersActivity extends AppCompatActivity {

    private final String getGroupMembersURL = GlobalVariables.API_URL + "group/getGroupMembers";
    private final String removeUserURL = GlobalVariables.API_URL + "group/removeUser";

    private MyAccountManager myAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        getGroupMembersHTTPRequest(new AddGroupModel(getIntent().getStringExtra("groupTitle"),"",myAccountManager.getToken()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,TodosActivity.class);
        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
        this.finish();
    }

    private void getGroupMembersHTTPRequest(AddGroupModel getGroupMembersModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(getGroupMembersModel);
        try{
            JSONObject getGroupMembersJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getGroupMembersURL,
                    getGroupMembersJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<UserRoleModel>>(){}.getType();
                                ArrayList<UserRoleModel> userRoleList = gson.fromJson(responseModel.getBody(),(listType));
                                fillMembersLayout(userRoleList);
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MembersActivity.this);
                            }
                            else
                                Toast.makeText(MembersActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MembersActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void removeUserHTTPRequest(AddGroupModel removeUserModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(removeUserModel);
        try{
            JSONObject removeUserJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    removeUserURL,
                    removeUserJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Toast.makeText(MembersActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MembersActivity.this,TodosActivity.class);
                                intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
                                intent.putExtra("accountManager",myAccountManager);
                                MembersActivity.this.startActivity(intent);
                                MembersActivity.this.finish();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MembersActivity.this);
                            }
                            else
                                Toast.makeText(MembersActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MembersActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void fillMembersLayout(ArrayList<UserRoleModel> userRoleList) {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams1);
        LinearLayout linearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);

        LinearLayout myLinearLayout = findViewById(R.id.membersLayout);
        myLinearLayout.setGravity(Gravity.CENTER);

        ArrayList<LinearLayout> components = new ArrayList<>();
        for(UserRoleModel userRole : userRoleList){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,250);
            final LinearLayout component = new LinearLayout(this);
            layoutParams.setMargins(100,150,100,0);
            component.setBackground(getDrawable(R.drawable.box));
            component.setLayoutParams(layoutParams);
            component.setOrientation(LinearLayout.HORIZONTAL);
            component.setPadding(0,0,40,0);

            final TextView textView = new TextView(this);
            textView.setText(userRole.getUsername());
            final LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsTextView.weight = 1;
            textView.setLayoutParams(layoutParamsTextView);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(25);
            textView.setTypeface(null, Typeface.BOLD);
            component.addView(textView,0);

            if(getIntent().getStringExtra("role").equals("admin") && !userRole.getUsername().equals(getIntent().getStringExtra("username"))){
                final Button buttonAddTodo = new Button(this);
                final LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParamsButton.weight = 2;
                buttonAddTodo.setLayoutParams(layoutParamsButton);
                buttonAddTodo.setText("Todo");
                buttonAddTodo.setBackgroundColor(Color.TRANSPARENT);
                buttonAddTodo.setTextColor(Color.WHITE);
                buttonAddTodo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MembersActivity.this,AddTodoActivity.class);
                        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
                        intent.putExtra("username",userRole.getUsername());
                        intent.putExtra("accountManager",myAccountManager);
                        intent.putExtra("role",getIntent().getStringExtra("role"));
                        MembersActivity.this.startActivity(intent);
                    }
                });
                component.addView(buttonAddTodo,1);

                final Button buttonRemoveUser = new Button(this);
                buttonRemoveUser.setLayoutParams(layoutParamsButton);
                buttonRemoveUser.setText("Sil");
                buttonRemoveUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeUserHTTPRequest(new AddGroupModel(getIntent().getStringExtra("groupTitle"),userRole.getUsername(),myAccountManager.getToken()));
                    }
                });
                buttonRemoveUser.setBackgroundColor(Color.TRANSPARENT);
                buttonRemoveUser.setTextColor(Color.WHITE);
                component.addView(buttonRemoveUser,2);
            }

            linearLayout.addView(component);
            components.add(component);
        }
        if(myLinearLayout != null){
            myLinearLayout.addView(scrollView);
        }
    }

}