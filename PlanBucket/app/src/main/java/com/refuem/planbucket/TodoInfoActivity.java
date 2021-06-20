package com.refuem.planbucket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.refuem.planbucket.Models.GetTodosModel;

public class TodoInfoActivity extends AppCompatActivity {

    private TextView TIUsernameTV,TITodoTitleTV, TITodoBodyTV;

    private void bindViews(){
        TIUsernameTV = findViewById(R.id.TIUsernameTV);
        TITodoTitleTV = findViewById(R.id.TITodoTitleTV);
        TITodoBodyTV = findViewById(R.id.TITodoBodyTV);
    }

    private void fillViews(GetTodosModel todoInfo){
        TIUsernameTV.setText(todoInfo.getUsername());
        TITodoTitleTV.setText(todoInfo.getTodoTitle());
        TITodoBodyTV.setText(todoInfo.getTodoBody());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_info);
        bindViews();
        GetTodosModel todoInfo = (GetTodosModel) getIntent().getExtras().getSerializable("todoInfo");
        fillViews(todoInfo);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height*.6));

    }
}