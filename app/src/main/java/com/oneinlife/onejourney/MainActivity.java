package com.oneinlife.onejourney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oneinlife.onejourney.Activities.LoginPage;

public class MainActivity extends AppCompatActivity {

  ImageView backgroundView;
  Button moveToLoginBtn;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    attachJavaObjectToXML();
    startAnimations();

    moveToLoginBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToLoginPage();
      }
    });
  }

  private void attachJavaObjectToXML() {
    try {
      backgroundView = findViewById(R.id.mainActivity_background);
      moveToLoginBtn = findViewById(R.id.mainActivity_startBtn);
    } catch (Exception e) {
      Toast.makeText(this, "MainAction:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void startAnimations() {
    try {
      backgroundView.animate().scaleX(2).scaleY(2).setDuration(10000).start();
    } catch (Exception e) {
      Toast.makeText(this, "MainAction animation:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void goToLoginPage() {
    try {
      startActivity(new Intent(this, LoginPage.class));
    } catch (Exception e) {
      Toast.makeText(this, "MainAction go login" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }
}
