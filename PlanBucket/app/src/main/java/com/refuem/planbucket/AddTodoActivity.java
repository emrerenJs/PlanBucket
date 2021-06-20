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
import com.refuem.planbucket.Configurations.GlobalVariables;
import com.refuem.planbucket.Models.AddTodoModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.UserRoleModel;

import org.json.JSONObject;

public class AddTodoActivity extends AppCompatActivity {

    private EditText todoTitleET,todoBodyET;
    private final String addTodoURL = GlobalVariables.API_URL + "group/addTodo";
    private MyAccountManager myAccountManager;

    private void bindViews(){
        todoTitleET = findViewById(R.id.todoTitleET);
        todoBodyET = findViewById(R.id.todoBodyET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        myAccountManager = getIntent().getParcelableExtra("accountManager");
        bindViews();
    }

    public void addTodoBTNOnClickListener(View v){
        if(todoTitleET.getText().toString().trim().equals("") || todoBodyET.getText().toString().trim().equals("") ){
            Toast.makeText(this, "Lütfen boşluk bırakmayınız..", Toast.LENGTH_SHORT).show();
        }else{
            addTodoHTTPRequest(new AddTodoModel(
                    getIntent().getStringExtra("groupTitle"),
                    getIntent().getStringExtra("username"),
                    todoTitleET.getText().toString(),
                    todoBodyET.getText().toString(),
                    myAccountManager.getToken()
            ));
        }
    }
    private void addTodoHTTPRequest(AddTodoModel addTodoModel){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(addTodoModel);
        try{
            JSONObject addTodoJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    addTodoURL,
                    addTodoJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Intent intent = new Intent(AddTodoActivity.this,MembersActivity.class);
                                intent.putExtra("groupTitle",getIntent().getStringExtra("groupTitle"));
                                intent.putExtra("accountManager",myAccountManager);
                                intent.putExtra("role",getIntent().getStringExtra("role"));
                                AddTodoActivity.this.startActivity(intent);
                                AddTodoActivity.this.finish();
                            }else if(responseModel.getStatus() == 401){
                                myAccountManager.logout(AddTodoActivity.this);
                            }
                            else
                                Toast.makeText(AddTodoActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(AddTodoActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}