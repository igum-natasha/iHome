package com.example.ihome_cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class MainActivity extends AppCompatActivity {

  private EditText etEmail, etPassword, etCountryCode;
  private Button btnLogin, btnRegister;

  private static final String TAG = "TuyaSmartHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initViews();

    btnLogin.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String countryCode = etCountryCode.getText().toString();
            TuyaHomeSdk.getUserInstance()
                .loginWithEmail(countryCode, email, password, loginCallback);
          }
        });
    btnRegister.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
          }
        });
  }

  private ILoginCallback loginCallback =
      new ILoginCallback() {
        @Override
        public void onSuccess(User user) {
          Toast.makeText(MainActivity.this, "login Successful", Toast.LENGTH_LONG).show();
          startActivity(new Intent(MainActivity.this, WifiLoginActivity.class));
        }

        @Override
        public void onError(String s, String s1) {
          Log.d(TAG, "login failed with error: " + s1);
          Toast.makeText(MainActivity.this, "login failed with error: " + s1, Toast.LENGTH_LONG)
              .show();
        }
      };

  private void initViews() {
    etCountryCode = findViewById(R.id.etCountryCode);
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    btnLogin = findViewById(R.id.btnLogin);
    btnRegister = findViewById(R.id.btnRegister);
  }
}
