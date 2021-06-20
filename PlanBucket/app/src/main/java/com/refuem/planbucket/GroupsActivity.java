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
import com.refuem.planbucket.Models.GroupInfoModel;
import com.refuem.planbucket.Models.ProfileInfoModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UsernameModel;
import com.refuem.planbucket.Services.NotifService;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    private final String getGroupsURL = GlobalVariables.API_URL + "group/myGroups";
    private MyAccountManager myAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        getGroupsHTTPRequest(new UsernameModel(myAccountManager.getUsername(),myAccountManager.getToken()));
    }

    public void goProfileOnClickHandler(View v){
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.putExtra("accountManager",myAccountManager);
        GroupsActivity.this.startActivity(intent);
        GroupsActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void getGroupsHTTPRequest(UsernameModel usernameModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(usernameModel);
        try{
            JSONObject usernameJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getGroupsURL,
                    usernameJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<GroupInfoModel>>(){}.getType();
                                ArrayList<GroupInfoModel> groupInfoModelList = gson.fromJson(responseModel.getBody(),(listType));
                                fillGroupsLayout(groupInfoModelList);
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(GroupsActivity.this);
                            }
                            else
                                Toast.makeText(GroupsActivity.this, "Bu grup ismi daha önce alınmış!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(GroupsActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void addNewGroupBTNOnClickHandler(View v){
        Intent intent = new Intent(this,NewGroupActivity.class);
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
    }

    private void fillGroupsLayout(ArrayList<GroupInfoModel> groups)
    {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams1);
        LinearLayout linearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);

        LinearLayout myLinearLayout = findViewById(R.id.groupsLayout);
        myLinearLayout.setGravity(Gravity.CENTER);
        ArrayList<TextView> myTextView = new ArrayList<>();
        for (GroupInfoModel group : groups) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,250);
            layoutParams.setMargins(100,100,100,0);
            final TextView rowTextView = new TextView(this);
            rowTextView.setText(group.getTitle());
            rowTextView.setLayoutParams(layoutParams);
            rowTextView.setBackground(getDrawable(R.drawable.box));
            rowTextView.setGravity(Gravity.CENTER);
            rowTextView.setTextColor(Color.WHITE);
            rowTextView.setTextSize(25);
            rowTextView.setTypeface(null, Typeface.BOLD);
            rowTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String groupTitle = rowTextView.getText().toString();
                    Intent intent = new Intent(GroupsActivity.this,TodosActivity.class);
                    intent.putExtra("groupTitle",groupTitle);
                    intent.putExtra("accountManager",myAccountManager);
                    GroupsActivity.this.startActivity(intent);
                }
            });
            linearLayout.addView(rowTextView);
            myTextView.add(rowTextView);
        }
        if (myLinearLayout != null) {
            myLinearLayout.addView(scrollView);
        }
    }
}