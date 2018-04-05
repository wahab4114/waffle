package com.example.user.waffle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button start_reg_btn;

    private Button start_login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        start_reg_btn = (Button) findViewById(R.id.start_reg_btn);

        start_login_btn = (Button)findViewById(R.id.start_login_btn);

        start_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });

        start_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(reg_intent);
            }
        });
    }
}
