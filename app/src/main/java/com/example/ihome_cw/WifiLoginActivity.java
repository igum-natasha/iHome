package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class WifiLoginActivity extends AppCompatActivity {

  private EditText etWifiLogin, etWifiPassword;
  private Button btnWifiVerify;
  private String email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wifi_login);

    Bundle bundle = getIntent().getExtras();
    initViews();
    if (bundle != null) {
      email = bundle.getString("Email");
    }

    btnWifiVerify.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String WifiLogin = etWifiLogin.getText().toString();
            String WifiPassword = etWifiPassword.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString("Email", email);
            bundle.putString("WifiLogin", WifiLogin);
            bundle.putString("WifiPassword", WifiPassword);
            Intent intent = new Intent(WifiLoginActivity.this, HomeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
  }

  private void initViews() {
    etWifiLogin = findViewById(R.id.etWifiLogin);
    etWifiPassword = findViewById(R.id.etWifiPassword);
    btnWifiVerify = findViewById(R.id.btnWifiVerify);
  }
}
