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
import com.refuem.planbucket.Models.GetTodosModel;
import com.refuem.planbucket.Models.GroupTitleModel;
import com.refuem.planbucket.Models.GroupTitleRequestModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UserRoleModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TodosActivity extends AppCompatActivity {

    private MyAccountManager myAccountManager;
    private final String getMyGroupInfoURL = GlobalVariables.API_URL + "group/getMyGroupInfo";
    private final String getTodosURL = GlobalVariables.API_URL + "group/getTodos";

    private ImageButton membersBTN, sendInviteBTN,myTodosBTN,messagesBTN;
    private String userRole;
    private TextView usernameInfoTV;

    private void bindViews(){
        membersBTN = findViewById(R.id.membersBTN);
        sendInviteBTN = findViewById(R.id.sendInviteBTN);
        myTodosBTN = findViewById(R.id.myTodosBTN);
        messagesBTN = findViewById(R.id.messagesBTN);
        usernameInfoTV = findViewById(R.id.usernameInfoTV);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);
        bindViews();
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        getMyGroupInfoHTTPRequest(new AddGroupModel(getIntent().getStringExtra("groupTitle"),myAccountManager.getUsername(),myAccountManager.getToken()));
        getTodosHTTPRequest(new GroupTitleRequestModel(getIntent().getStringExtra("groupTitle"),myAccountManager.getToken()));
    }

    public void SendInviteBTNOnClickHandler(View v){
        Intent intent = new Intent(this,InviteActivity.class);
        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
    }

    private void getMyGroupInfoHTTPRequest(AddGroupModel addGroupModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(addGroupModel);
        try{
            JSONObject addGroupJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getMyGroupInfoURL,
                    addGroupJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                UserRoleModel userRoleModel = gson.fromJson(String.valueOf(responseModel.getBody()),UserRoleModel.class);
                                userRole = userRoleModel.getRole();
                                usernameInfoTV.setText(userRoleModel.getUsername());
                                if(userRoleModel.getRole().equals("admin")){
                                    sendInviteBTN.setVisibility(View.VISIBLE);
                                }else{
                                    sendInviteBTN.setVisibility(View.GONE);
                                }
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(TodosActivity.this);
                            }
                            else
                                Toast.makeText(TodosActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(TodosActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,GroupsActivity.class);
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
        this.finish();
    }

    private void fillTodos(ArrayList<GetTodosModel> todos){
        LinearLayout todosLayout = findViewById(R.id.todosLayout);
        LinearLayout finishedLayout = findViewById(R.id.finishedLayout);
        for(GetTodosModel todo : todos){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            final LinearLayout component = new LinearLayout(this);
            layoutParams.setMargins(10,80,10,0);
            component.setLayoutParams(layoutParams);
            //component.setPadding(0,50,0,50);
            component.setOrientation(LinearLayout.VERTICAL);

            final TextView textView = new TextView(this);
            textView.setText(todo.getTodoTitle());
            final LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsTextView.setMargins(10,50,10,50);
            textView.setLayoutParams(layoutParamsTextView);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setTypeface(null, Typeface.BOLD);
            component.addView(textView);

            component.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TodosActivity.this,TodoInfoActivity.class);
                    intent.putExtra("todoInfo",todo);
                    TodosActivity.this.startActivity(intent);
                }
            });

            if(todo.isFinished()){
                component.setBackground(getDrawable(R.drawable.todobox));
                finishedLayout.addView(component);
            }
            else{
                component.setBackground(getDrawable(R.drawable.finishedbox));
                todosLayout.addView(component);
            }
        }
    }

    private void getTodosHTTPRequest(GroupTitleRequestModel groupTitleRequestModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(groupTitleRequestModel);
        try{
            JSONObject groupTitleRequestJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getTodosURL,
                    groupTitleRequestJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<GetTodosModel>>(){}.getType();
                                ArrayList<GetTodosModel> getTodosModel = gson.fromJson(responseModel.getBody(),(listType));
                                fillTodos(getTodosModel);
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(TodosActivity.this);
                            }
                            else
                                Toast.makeText(TodosActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(TodosActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void myTodosBTNOnClickHandler(View v){
        Intent intent = new Intent(this,MyTodoActivity.class);
        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);

    }

    public void membersBTNOnClickHandler(View v){
        Intent intent = new Intent(this,MembersActivity.class);
        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
        intent.putExtra("accountManager",myAccountManager);
        intent.putExtra("role",userRole);
        startActivity(intent);
    }

    public void messagesBTNOnClickHandler(View v){
        Intent intent = new Intent(this,MessageActivity.class);
        myAccountManager.stopNotificationService(this);
        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
    }
}