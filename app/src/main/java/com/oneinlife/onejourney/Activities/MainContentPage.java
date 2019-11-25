package com.oneinlife.onejourney.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oneinlife.onejourney.Fragments.ShareRoom;
import com.oneinlife.onejourney.Fragments.ShareJourney;
import com.oneinlife.onejourney.Fragments.ShareVehicle;
import com.oneinlife.onejourney.R;


public class MainContentPage extends AppCompatActivity {

  //Fragments Objects
  private ShareJourney objShareJourney;
  private ShareRoom objShareRoom;
  private ShareVehicle objShareVehicle;

  private Toolbar objToolbar;
  private NavigationView objNavigationView;
  private DrawerLayout objDrawerLayout;

  private ImageView header_profile;
  private TextView header_userName, header_userEmail;
  //private ProgressBar progressBar;
  private BottomNavigationView objBottomNavigationView;

  //Firebase
  private FirebaseAuth objFirebaseAuth;
  private FirebaseFirestore objFirebaseFirestore;
  private DocumentReference objDocumentReference;

  //Class Variables
  private String currentUserEmail;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_content_page);
    objFirebaseAuth = FirebaseAuth.getInstance();
    objFirebaseFirestore = FirebaseFirestore.getInstance();

    objShareJourney = new ShareJourney();
    objShareRoom = new ShareRoom();
    objShareVehicle = new ShareVehicle();
    changeFragment(objShareJourney);

    attachJavaObjectToXML();
    setUpDrawerMenu();

    getCurrentUserInfo();

    /*
    objBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem item) {
        try
        {
          switch (item.getItemId())
          {
            case R.id.item_shareJourney:
              changeFragment(objShareJourney);
              return true;

            case R.id.item_shareVehicle:
              changeFragment(objShareVehicle);
              return true;

            case R.id.item_shareRoom:
              changeFragment(objShareRoom);
              return true;

              default:
                return false;
          }
        }
        catch (Exception e)
        {
          Toast.makeText(MainContentPage.this, "Main Content"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
      }
    });
     */
  }

  private void changeFragment(Fragment objFragment)
  {
    try {

      FragmentTransaction objFragmentTransaction = getSupportFragmentManager().beginTransaction();
      objFragmentTransaction.replace(R.id.container, objFragment);

      objFragmentTransaction.commit();

      }
      catch(Exception e)
      {
        Toast.makeText(this, "Main Content Page"+e.getMessage(), Toast.LENGTH_SHORT).show();
      }
  }

  private void attachJavaObjectToXML() {
    try {
      objToolbar = findViewById(R.id.toolBar);
      objDrawerLayout = findViewById(R.id.drawerLayout);
      objNavigationView = findViewById(R.id.navigationView);

      View headerXMLFile = objNavigationView.getHeaderView(0);
      header_profile = headerXMLFile.findViewById(R.id.header_profile);
      header_userName = headerXMLFile.findViewById(R.id.header_userName);
      header_userEmail = headerXMLFile.findViewById(R.id.header_userEmail);
      // progressBar = headerXMLFile.findViewById(R.id.header_progressBar);
      objBottomNavigationView = headerXMLFile.findViewById(R.id.bottomNavBar);

    } catch (Exception e) {
      Toast.makeText(this, "MainContentPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void setUpDrawerMenu() {
    try {
      ActionBarDrawerToggle objActionBarDrawer = new ActionBarDrawerToggle(
          this, objDrawerLayout, objToolbar, R.string.open, R.string.close);

      objDrawerLayout.addDrawerListener(objActionBarDrawer);
      objActionBarDrawer.syncState();
    } catch (Exception e) {
      Toast.makeText(this, "MainContentPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void openDrawer() {
    try {
      objDrawerLayout.openDrawer(GravityCompat.START);
    } catch (Exception e) {
      Toast.makeText(this, "MainContentPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void closeDrawer() {
    try {
      objDrawerLayout.closeDrawer(GravityCompat.START);
    } catch (Exception e) {
      Toast.makeText(this, "MainContentPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void getCurrentUserInfo() {
    try {
      currentUserEmail = getCurrentUser();
      if (currentUserEmail.equals("No user is logged in")) {
        Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginPage.class));
        finish();
      } else {
        //progressBar.setVisibility(View.VISIBLE);
        objDocumentReference = objFirebaseFirestore.collection("UserProfileData")
            .document(currentUserEmail);
        objDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot documentSnapshot) {
            header_userName.setText(documentSnapshot.getString("user_nickname"));

            header_userEmail.setText(currentUserEmail);
            header_userName.setAllCaps(true);

            String linkOfProfileImage = documentSnapshot.getString("profile_image_url");

            Glide.with(MainContentPage.this).load(linkOfProfileImage)
                .into(header_profile);
            //Glide.with(MainContentPage.this).load(linkOfProfileImage).into(header_backgroundProfile);
          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

          }
        });
      }

    } catch (Exception e) {
      Toast.makeText(this, "MainContentPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private String getCurrentUser() {
    try {
      if (objFirebaseAuth != null) {
        return objFirebaseAuth.getCurrentUser().getEmail().toString();
      } else {
        return "No user is logged in";
      }


    } catch (Exception e) {
      Toast.makeText(this, "MainContentPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
      return null;
    }

  }
}
