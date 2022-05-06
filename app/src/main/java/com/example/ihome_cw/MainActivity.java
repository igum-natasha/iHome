package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

public class MainActivity extends AppCompatActivity {

  private EditText etEmail, etPassword, etCountryCode;
  private Button btnLogin;
  private String email, password, countryCode;
  private static final String TAG = "TuyaSmartHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Bundle bundle = getIntent().getExtras();

    if (bundle != null) {
      email = bundle.getString("Email");
      password = bundle.getString("Password");
      countryCode = bundle.getString("CountryCode");
      TuyaHomeSdk.getUserInstance().loginWithEmail(countryCode, email, password, loginCallback);
    } else {
      Intent intent = new Intent(MainActivity.this, PreMainActivity.class);
      startActivity(intent);
    }
  }

  private ILoginCallback loginCallback =
      new ILoginCallback() {

        @Override
        public void onSuccess(com.tuya.smart.android.user.bean.User user) {
          Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_LONG).show();
          Bundle bundle = new Bundle();
          bundle.putString("Email", email);
          Intent intent = new Intent(MainActivity.this, WifiLoginActivity.class);
          intent.putExtras(bundle);
          startActivity(intent);
        }

        @Override
        public void onError(String s, String s1) {
          Toast.makeText(MainActivity.this, "login failed with error: " + s1, Toast.LENGTH_LONG)
              .show();
        }
      };
}
