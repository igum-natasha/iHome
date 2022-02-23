package com.example.ihome_cw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

public class RegistrationActivity extends AppCompatActivity {

  private EditText etRegEmail, etRegPassword, etRegCountryCode, etRegVarificationCode;
  private Button btnVarificationCode, btnRegister;

  private static final String TAG = "TuyaSmartHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registration);

    initViews();

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
          Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_LONG)
              .show();
          startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
        }

        @Override
        public void onError(String s, String s1) {
          Log.d(TAG, "Registration failed with error" + s1);
          Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_LONG)
              .show();
        }
      };

  IResultCallback validateCallback =
      new IResultCallback() {
        @Override
        public void onError(String s, String s1) {
          Log.d(TAG, "Verification code failed with error" + s1);
          Toast.makeText(
                  RegistrationActivity.this, "Failed to sent verification code!", Toast.LENGTH_LONG)
              .show();
        }

        @Override
        public void onSuccess() {
          Toast.makeText(
                  RegistrationActivity.this,
                  "Successfully sent verification code!",
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
    etRegCountryCode = findViewById(R.id.etRegCountryCode);
    etRegVarificationCode = findViewById(R.id.etVarificationCode);
    etRegEmail = findViewById(R.id.etRegEmail);
    etRegPassword = findViewById(R.id.etRegPassword);
    btnVarificationCode = findViewById(R.id.btnValidate);
    btnRegister = findViewById(R.id.btnUserRegister);
  }
}