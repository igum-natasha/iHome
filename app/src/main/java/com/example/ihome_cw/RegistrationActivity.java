package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

public class RegistrationActivity extends AppCompatActivity {

  EditText etRegEmail, etRegPassword, etRegCountryCode, etRegVarificationCode;
  Button btnVarificationCode, btnRegister;
  ImageButton btnBack;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registration);

    initViews();
      btnBack.setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      finish();
                  }
              });
    etRegVarificationCode.setVisibility(View.INVISIBLE);
    btnRegister.setVisibility(View.INVISIBLE);

    btnVarificationCode.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String registeredEmail = etRegEmail.getText().toString();
            String registeredCountryCode = etRegCountryCode.getText().toString();
            getValidationCode(registeredCountryCode, registeredEmail);
          }
        });

    btnRegister.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String registeredEmail = etRegEmail.getText().toString();
            String registeredCountryCode = etRegCountryCode.getText().toString();
            String registeredPassword = etRegPassword.getText().toString();
            String inputVerificationCode = etRegVarificationCode.getText().toString();

            TuyaHomeSdk.getUserInstance()
                .registerAccountWithEmail(
                    registeredCountryCode,
                    registeredEmail,
                    registeredPassword,
                    inputVerificationCode,
                    registerCallback);
          }
        });
  }

  IRegisterCallback registerCallback =
      new IRegisterCallback() {
        @Override
        public void onSuccess(User user) {
          Toast.makeText(
                  RegistrationActivity.this,
                  getResources().getString(R.string.regist_suc),
                  Toast.LENGTH_LONG)
              .show();
          startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        }

        @Override
        public void onError(String s, String s1) {
          Toast.makeText(
                  RegistrationActivity.this,
                  getResources().getString(R.string.regist_fail) + " " + s1,
                  Toast.LENGTH_LONG)
              .show();
          startActivity(new Intent(RegistrationActivity.this, PreMainActivity.class));
        }
      };

  IResultCallback validateCallback =
      new IResultCallback() {
        @Override
        public void onError(String s, String s1) {
          Toast.makeText(
                  RegistrationActivity.this,
                  getResources().getString(R.string.ver_code_fail) + " " + s1,
                  Toast.LENGTH_LONG)
              .show();
          startActivity(new Intent(RegistrationActivity.this, PreMainActivity.class));
        }

        @Override
        public void onSuccess() {
          Toast.makeText(
                  RegistrationActivity.this,
                  getResources().getString(R.string.ver_code_suc),
                  Toast.LENGTH_LONG)
              .show();
          etRegVarificationCode.setVisibility(View.VISIBLE);
          btnRegister.setVisibility(View.VISIBLE);
        }
      };

  private void getValidationCode(String countryCode, String email) {
    TuyaHomeSdk.getUserInstance()
        .getRegisterEmailValidateCode(countryCode, email, validateCallback);
  }

  private void initViews() {
      btnBack = findViewById(R.id.back_icon);
    etRegCountryCode = findViewById(R.id.etRegCountryCode);
    etRegVarificationCode = findViewById(R.id.etVarificationCode);
    etRegEmail = findViewById(R.id.etRegEmail);
    etRegPassword = findViewById(R.id.etRegPassword);
    btnVarificationCode = findViewById(R.id.btnValidate);
    btnRegister = findViewById(R.id.btnUserRegister);
  }
}
