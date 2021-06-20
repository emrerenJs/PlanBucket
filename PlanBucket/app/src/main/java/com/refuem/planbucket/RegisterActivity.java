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
import com.refuem.planbucket.Models.RegisterModel;
import com.refuem.planbucket.Models.ResponseModel;
import com.refuem.planbucket.Models.User;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText rUsernameET,rPasswordET,rAgainPasswordET,rEmailET;
    private final String registerURL = GlobalVariables.API_URL + "auth/register";

    private void bindViews(){
        rUsernameET = findViewById(R.id.rUsernameET);
        rPasswordET = findViewById(R.id.rPasswordET);
        rAgainPasswordET = findViewById(R.id.rAgainPasswordET);
        rEmailET = findViewById(R.id.rEmailET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindViews();
    }

    private boolean isEmpty(){
        if(
                rUsernameET.getText().toString().trim().equals("") ||
                rPasswordET.getText().toString().trim().equals("") ||
                rAgainPasswordET.getText().toString().trim().equals("") ||
                rEmailET.getText().toString().trim().equals("")
        )
            return true;
        return false;
    }
    
    private boolean isPasswordsSame(){
        if(rPasswordET.getText().toString().equals(rAgainPasswordET.getText().toString()))
            return true;
        return false;
    }

    private boolean isCorrect(){
        if(rPasswordET.getText().toString().length() >= 6 && rPasswordET.getText().toString().length() <= 12)
            return true;
        return false;
    }

    public void registerBTNOnClickHandler(View v){
        if(isEmpty()){
            Toast.makeText(this, "Lütfen boş yer bırakmayınız.", Toast.LENGTH_SHORT).show();
        }else{
            if(!isPasswordsSame()){
                Toast.makeText(this, "Parolalarınız eşleşmiyor..", Toast.LENGTH_SHORT).show();
            }else if(!isCorrect()){
                Toast.makeText(this, "Parolanız 6 ile 12 karakter arasında olabilir!", Toast.LENGTH_SHORT).show();
            }
            else{
                RegisterModel registerModel = new RegisterModel(rUsernameET.getText().toString(),rPasswordET.getText().toString(),rEmailET.getText().toString());
                registerPageHTTPRequest(registerModel);
            }
        }
    }

    public void loginViewBTNOnClickHandler(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerPageHTTPRequest(RegisterModel registerModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Gson gson = new Gson();
        String jsonString = gson.toJson(registerModel);
        try{
            JSONObject registerJSON = new JSONObject(jsonString);
            JsonObjectRequest registerPageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    registerURL,
                    registerJSON,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ResponseModel responseModel = gson.fromJson(String.valueOf(response),ResponseModel.class);
                            if(responseModel.getStatus() == 200){
                                Toast.makeText(RegisterActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                RegisterActivity.this.finish();
                            }
                            else
                                Toast.makeText(RegisterActivity.this, responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Rest Response : ", error.toString());
                            Toast.makeText(RegisterActivity.this, "Servis bağlantısı başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(registerPageRequest);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}