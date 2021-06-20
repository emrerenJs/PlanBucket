package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.reflect.TypeToken;
import com.refuem.planbucket.Configurations.GlobalVariables;
import com.refuem.planbucket.Models.AddGroupModel;
import com.refuem.planbucket.Models.FinishTodoModel;
import com.refuem.planbucket.Models.GetTodosModel;
import com.refuem.planbucket.Models.GroupTitleModel;
import com.refuem.planbucket.Models.ResponseModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MyTodoActivity extends AppCompatActivity {

    LinearLayout linearLayout,targetLayout;
    int count = 0;

    private final String getMyTodosURL = GlobalVariables.API_URL + "group/getMyTodos";
    private final String finishTodoURL = GlobalVariables.API_URL + "group/finishTodo";

    private MyAccountManager myAccountManager;

    private void fillTodos(ArrayList<GetTodosModel> todos){
        for(GetTodosModel todo : todos){
            final TextView todoTV = new TextView(this);
            todoTV.setText(todo.getTodoTitle());
            todoTV.setGravity(Gravity.CENTER);
            todoTV.setTextSize(20);
            final LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTextView.setMargins(10,30,10,30);
            todoTV.setPadding(0,30,0,30);
            todoTV.setTypeface(null, Typeface.BOLD);
            todoTV.setLayoutParams(layoutParamsTextView);
            if(todo.isFinished()){
                todoTV.setBackground(getDrawable(R.drawable.todobox));
            }else{
                todoTV.setBackground(getDrawable(R.drawable.finishedbox));
            }
            todoTV.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    String label = "";
                    if(todo.isFinished()){
                        label =  "finished";
                    }else{
                        label = "todo";
                    }
                    String[] todoPrimary = new String[]{todoTV.getText().toString(),label};
                    ClipData data = ClipData.newPlainText(label,"");
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(todoTV);
                    v.startDragAndDrop(data, shadow, todoPrimary, 0);
                    return false;
                }
            });
            if(!todo.isFinished()){
                linearLayout.addView(todoTV);
            }else{
                targetLayout.addView(todoTV);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,TodosActivity.class);
        intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
        intent.putExtra("accountManager",myAccountManager);
        startActivity(intent);
        this.finish();
    }

    private void getMyTodosHTTPRequest(AddGroupModel getMyTodosModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(getMyTodosModel);
        try{
            JSONObject getMyTodosJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getMyTodosURL,
                    getMyTodosJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Type listType = new TypeToken<ArrayList<GetTodosModel>>(){}.getType();
                                ArrayList<GetTodosModel> getTodosModel = gson.fromJson(responseModel.getBody(),(listType));
                                fillTodos(getTodosModel);
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MyTodoActivity.this);
                            }else if(responseModel.getStatus() == 100){
                                Toast.makeText(MyTodoActivity.this, "Todo yok!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(MyTodoActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MyTodoActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void finishTodoHTTPRequest(FinishTodoModel finishTodoModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(finishTodoModel);
        try{
            JSONObject finishTodoJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    finishTodoURL,
                    finishTodoJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Intent intent = new Intent(MyTodoActivity.this,MyTodoActivity.class);
                                intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
                                intent.putExtra("accountManager",myAccountManager);
                                MyTodoActivity.this.startActivity(intent);
                                MyTodoActivity.this.finish();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(MyTodoActivity.this);
                            }
                            else
                                Toast.makeText(MyTodoActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(MyTodoActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_my_todo);
        linearLayout = (LinearLayout) findViewById(R.id.bottomLinear);
        targetLayout = (LinearLayout) findViewById(R.id.targetLinear);
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        getMyTodosHTTPRequest(
                new AddGroupModel(
                        getIntent().getStringExtra("groupTitle"),
                        myAccountManager.getUsername(),
                        myAccountManager.getToken()
                )
        );
        linearLayout.setOnDragListener(new View.OnDragListener(){
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch (action){
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DROP:{
                        break;
                    }
                    case DragEvent.ACTION_DRAG_ENDED:{
                        String[] parsed = (String[]) event.getLocalState();
                        if(event.getX() > 0 && parsed[1].equals("todo")){
                            finishTodoHTTPRequest(new FinishTodoModel(
                                    getIntent().getStringExtra("groupTitle"),
                                    parsed[0],
                                    myAccountManager.getToken(),
                                    true
                            ));
                        }else if(event.getX() == 0 && parsed[1].equals("finished")){
                            finishTodoHTTPRequest(new FinishTodoModel(
                                    getIntent().getStringExtra("groupTitle"),
                                    parsed[0],
                                    myAccountManager.getToken(),
                                    false
                            ));
                        }
                    }
                    default:
                        break;
                }
                return true;
            }
        });
    }
}