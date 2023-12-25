package com.mekaar.presensiku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AwalActivity extends AppCompatActivity {

    Button registerbtn, loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awal);

        registerbtn = findViewById(R.id.btn_Register);
        loginbtn = findViewById(R.id.btn_login);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AwalActivity.this, RegisterActivity.class));

            }
        });

        //handle login btn on click
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AwalActivity.this, LoginActivity.class));

            }
        });

    }
}