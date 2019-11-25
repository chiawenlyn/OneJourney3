package com.oneinlife.onejourney.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.oneinlife.onejourney.R;

public class LoginPage extends AppCompatActivity {

  private Button loginBtn;
  private Button signUpBtn;
  private Button forgetBtn;
  private EditText loginPageEmailET, loginPagePasswordET;


  //Firebase variables
  private FirebaseAuth objFirebaseAuth;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login_page);

    objFirebaseAuth = FirebaseAuth.getInstance();
    attachJavaObjectToXML();
  }

  private void attachJavaObjectToXML() {
    try {
      loginPageEmailET = findViewById(R.id.UsernameInput);
      loginPagePasswordET = findViewById(R.id.PasswordInput);
      loginBtn = findViewById(R.id.button_login);
      signUpBtn = findViewById(R.id.button_signup);
      forgetBtn = findViewById(R.id.button_forgetPassword);

      signUpBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          moveToRegisterPage();
        }
      });

      loginBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          signInUser();
        }
      });

      forgetBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          forgetPassword();
        }
      });

    } catch (Exception e) {
      Toast.makeText(this, "LoginPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }


  private void signInUser() {
    try {
      if (!loginPageEmailET.getText().toString().isEmpty()
          && !loginPagePasswordET.getText().toString().isEmpty()) {
        if (objFirebaseAuth.getCurrentUser() == null) {
          objFirebaseAuth.signInWithEmailAndPassword(
              loginPageEmailET.getText().toString(),
              loginPagePasswordET.getText().toString()
          )
              .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                  Toast.makeText(LoginPage.this, "Welcome," + loginPageEmailET.getText().toString(),
                      Toast.LENGTH_SHORT).show();

                  startActivity(new Intent(LoginPage.this, MainContentPage.class));
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(LoginPage.this, "Login in failed" + e.getMessage(),
                      Toast.LENGTH_SHORT).show();
                }
              })
          ;
        } else {
          objFirebaseAuth.signOut();
          Toast.makeText(this, "Previous user logged out successfully", Toast.LENGTH_SHORT).show();
        }
      }

    } catch (Exception e) {
      Toast.makeText(this, "LoginPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void moveToRegisterPage() {
    try {
      startActivity(new Intent(this, RegisterPage.class));

    } catch (Exception e) {
      Toast.makeText(this, "LoginPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void forgetPassword() {
    try {
      startActivity(new Intent(this, ForgetPage.class));
    } catch (Exception e) {
      Toast.makeText(this, "LoginPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }
}
