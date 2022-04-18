package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WifiLoginActivity extends AppCompatActivity {

  private EditText etWifiLogin, etWifiPassword;
  private Button btnWifiVerify;
  private String email;
  Dialog wifiDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wifi_login);

    Bundle bundle = getIntent().getExtras();
    initViews();
    defineWifiDialog();
    if (bundle != null) {
      email = bundle.getString("Email");
    }

    btnWifiVerify.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                String WifiLogin = etWifiLogin.getText().toString();
                String WifiPassword = etWifiPassword.getText().toString();

//                try {
//                  if (!checkWifiConnection(WifiLogin, WifiPassword)) {
//                    wifiDialog.show();
//                  } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("Email", email);
                    bundle.putString("WifiLogin", WifiLogin);
                    bundle.putString("WifiPassword", WifiPassword);
                    Toast.makeText(WifiLoginActivity.this, WifiLogin+WifiPassword, Toast.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(WifiLoginActivity.this, HomeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                  }
//                } catch (InterruptedException e) {
//                  e.printStackTrace();
//                }
              }
            });
  }

  @SuppressLint("NewApi")
  private boolean checkWifiConnection(String ssid, String password) throws InterruptedException {
    boolean state = false;
    WifiConfiguration wifiConfig = new WifiConfiguration();
    wifiConfig.SSID = String.format("\"%s\"", ssid);
    wifiConfig.preSharedKey = String.format("\"%s\"", password);

    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
    wifiConfig.status = WifiConfiguration.Status.ENABLED;


    WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);

//    int netId = wifiManager.addNetwork(wifiConfig);
    wifiManager.disconnect();
//    wifiManager.enableNetwork(netId, true);
    boolean isConnected = wifiManager.reconnect();
    state = wifiManager.enableNetwork(wifiConfig.networkId, true);
    return state;
  }
  private void defineWifiDialog() {
    wifiDialog = new Dialog(WifiLoginActivity.this);
    wifiDialog.setContentView(R.layout.wifi_dialog);
    wifiDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    wifiDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    wifiDialog.setCancelable(false);

    Button ok = wifiDialog.findViewById(R.id.btn_retry);
    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        wifiDialog.dismiss();
        Intent intent = new Intent(WifiLoginActivity.this, WifiLoginActivity.class);
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
