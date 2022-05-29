package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class LoginActivity extends AppCompatActivity {

  EditText etEmail, etPassword, etCountryCode;
  Button btnLogin;
  ImageButton btnBack;
  private String email, password, countryCode;
  private static final String TAG = "TuyaSmartHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    initViews();
    etCountryCode.setVisibility(View.INVISIBLE);
    etEmail.setVisibility(View.INVISIBLE);
    etPassword.setVisibility(View.INVISIBLE);
    btnBack.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                finish();
              }
            });
    Bundle bundle = getIntent().getExtras();

    if (bundle != null) {
      email = bundle.getString("Email");
      password = bundle.getString("Password");
      countryCode = bundle.getString("CountryCode");
      TuyaHomeSdk.getUserInstance().loginWithEmail(countryCode, email, password, loginCallback);
    } else {
      etCountryCode.setVisibility(View.VISIBLE);
      etEmail.setVisibility(View.VISIBLE);
      etPassword.setVisibility(View.VISIBLE);
    }

    btnLogin.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            countryCode = etCountryCode.getText().toString();
            TuyaHomeSdk.getUserInstance()
                .loginWithEmail(countryCode, email, password, loginCallback);
          }
        });
  }

  private ILoginCallback loginCallback =
      new ILoginCallback() {
        @Override
        public void onSuccess(com.tuya.smart.android.user.bean.User user) {
          Toast.makeText(
                  LoginActivity.this,
                  getResources().getString(R.string.login_suc),
                  Toast.LENGTH_LONG)
              .show();
          AppDatabase db = AppDatabase.build(getApplicationContext());
          com.example.ihome_cw.User user1 = new com.example.ihome_cw.User();
          user1.setEmail(email);
          user1.setCountryCode(countryCode);
          user1.setPassword(password);
          db.userDao().insertUser(user1);
          Bundle bundle = new Bundle();
          bundle.putString("Email", email);
          Intent intent = new Intent(LoginActivity.this, WifiLoginActivity.class);
          intent.putExtras(bundle);
          startActivity(intent);
        }

        @Override
        public void onError(String s, String s1) {
          Toast.makeText(
                  LoginActivity.this,
                  getResources().getString(R.string.login_fail) + s1,
                  Toast.LENGTH_LONG)
              .show();
        }
      };

  private void initViews() {
    btnBack = findViewById(R.id.back_icon);
    etCountryCode = findViewById(R.id.etCountryCode);
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    btnLogin = findViewById(R.id.btnLogin);
  }
}
