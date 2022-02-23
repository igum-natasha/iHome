package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PreMainActivity extends AppCompatActivity {

    private Button btnPreLogin, btnPreRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);

        btnPreLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PreMainActivity.this, MainActivity.class));
                    }
                });
        btnPreRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PreMainActivity.this, RegistrationActivity.class));
                    }
                });
    }
    private void initViews() {
        btnPreLogin = findViewById(R.id.btnPreLogin);
        btnPreRegister = findViewById(R.id.btnPreRedister);
    }
}