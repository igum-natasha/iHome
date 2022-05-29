package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class WifiLoginActivity extends AppCompatActivity {

  EditText etWifiLogin, etWifiPassword;
  Button btnWifiVerify;
  String email;
  ImageButton btnBack;
  Dialog wifiDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wifi_login);

    Bundle bundle = getIntent().getExtras();
    initViews();
    defineWifiDialog();
    btnBack.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(WifiLoginActivity.this, PreMainActivity.class);
            startActivity(intent);
          }
        });
    if (bundle != null) {
      email = bundle.getString("Email");
    }

    btnWifiVerify.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            btnWifiVerify.setText(getResources().getString(R.string.check));
            String WifiLogin = etWifiLogin.getText().toString();
            String WifiPassword = etWifiPassword.getText().toString();
            if (!checkWifiConnection(WifiLogin, WifiPassword)) {
              wifiDialog.show();
            } else {
              Bundle bundle = new Bundle();
              bundle.putString("Email", email);
              bundle.putString("WifiLogin", WifiLogin);
              bundle.putString("WifiPassword", WifiPassword);
              Intent intent = new Intent(WifiLoginActivity.this, HomeActivity.class);
              intent.putExtras(bundle);
              startActivity(intent);
            }
          }
        });
  }

  @SuppressLint("NewApi")
  private boolean checkWifiConnection(String ssid, String password) {
    boolean state;
    btnWifiVerify.setText(getResources().getString(R.string.check));
    WifiConfiguration wifiConfig = new WifiConfiguration();
    wifiConfig.SSID = String.format("\"%s\"", ssid);
    wifiConfig.preSharedKey = String.format("\"%s\"", password);

    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

    WifiManager wifiManager =
        (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
    wifiManager.disconnect();

    boolean recon = wifiManager.reconnect();
    state = checkWifiNegotiation(wifiManager, wifiConfig.networkId);
    return recon && state;
  }

  private static boolean checkWifiNegotiation(WifiManager wifiManager, int netId) {
    boolean successful = false;
    for (int i = 0; i < 30; i++) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      successful = wifiManager.enableNetwork(netId, true);
    }
    // no matter what happened above, if COMPLETED then we have the correct pw
    if (!successful
        && wifiManager.getConnectionInfo().getSupplicantState().equals(SupplicantState.COMPLETED)) {
      successful = true;
    }

    return successful;
  }

  private void defineWifiDialog() {
    wifiDialog = new Dialog(WifiLoginActivity.this);
    wifiDialog.setContentView(R.layout.wifi_dialog);
    wifiDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    wifiDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    wifiDialog.setCancelable(false);

    Button ok = wifiDialog.findViewById(R.id.btn_retry);
    ok.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            wifiDialog.dismiss();
            Intent intent = new Intent(WifiLoginActivity.this, WifiLoginActivity.class);
            startActivity(intent);
          }
        });
  }

  private void initViews() {
    btnBack = findViewById(R.id.back_icon);
    etWifiLogin = findViewById(R.id.etWifiLogin);
    etWifiPassword = findViewById(R.id.etWifiPassword);
    btnWifiVerify = findViewById(R.id.btnWifiVerify);
  }
}
