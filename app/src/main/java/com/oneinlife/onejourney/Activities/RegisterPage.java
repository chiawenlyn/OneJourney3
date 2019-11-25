package com.oneinlife.onejourney.Activities;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oneinlife.onejourney.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterPage extends AppCompatActivity {

  // XML Object
  private CircleImageView profileIV;
  private EditText userEmailET, userNicknameET, userPasswordET, userBirthday;

  private Button registerBtn;
  private RadioGroup objRadioGroup;
  private RadioButton objRadioButton;

  //Class variables
  private Uri profileImageURL;
  private static int REQUEST_CODE = 1;
  private int radioId;

  private DatePickerDialog.OnDateSetListener objectOnDateListener;


  //Firebase Variables
  private FirebaseFirestore objFirebaseFireStore;
  private FirebaseAuth objFirebaseAuth;
  private StorageReference objStorageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_page);

    attachJavaToXMLObject();

    objFirebaseFireStore = FirebaseFirestore.getInstance();
    objFirebaseAuth = FirebaseAuth.getInstance();

    objStorageReference = FirebaseStorage.getInstance().getReference("imageFolder");

    registerBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createUserAccount();
      }
    });
  }

  private void createUserAccount() {
    try {
      if (objFirebaseAuth.getCurrentUser() != null) {
        objFirebaseAuth.signOut();
      }
      if (objFirebaseAuth.getCurrentUser() == null
          && !userEmailET.getText().toString().isEmpty()
          && !userEmailET.getText().toString().isEmpty()
          && !userPasswordET.getText().toString().isEmpty()
          && profileImageURL != null) {
        Toast.makeText(this, "Registering the user", Toast.LENGTH_SHORT).show();
        objFirebaseAuth.createUserWithEmailAndPassword(userEmailET.getText().toString()
            , userPasswordET.getText().toString())
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
              @Override
              public void onSuccess(AuthResult authResult) {
                uploadUserToFirebase();
              }
            }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(RegisterPage.this, "Fails to create user:" + e.getMessage(),
                Toast.LENGTH_SHORT).show();
          }
        });

      } else {
        Toast.makeText(this, "Please check user data fields and profile images", Toast.LENGTH_SHORT).show();
      }


    } catch (Exception e) {
      Toast.makeText(this, "Register Firebase" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }


  private void uploadUserToFirebase() {
    try {
      if (profileImageURL != null) {
        String imageName = userEmailET.getText().toString() + "." + getExtension(profileImageURL);
        final StorageReference imageRef = objStorageReference.child(imageName);

        Toast.makeText(this, "Uploading user profile", Toast.LENGTH_SHORT).show();
        UploadTask objectUploadTask = imageRef.putFile(profileImageURL);
        objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
          @Override
          public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
            if (!task.isSuccessful()) {
              throw task.getException();
            }

            return imageRef.getDownloadUrl();
          }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
          @Override
          public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
              Toast.makeText(RegisterPage.this, "Uploading user information", Toast.LENGTH_SHORT)
                  .show();
              Map<String, Object> objectMap = new HashMap<>();
              objectMap.put("profile_image_url", task.getResult().toString());

              objectMap.put("user_nickname", userNicknameET.getText().toString());
              objectMap.put("user_email", userEmailET.getText().toString());

              objectMap.put("dob", userBirthday.getText().toString());
              objectMap.put("user_password", userPasswordET.getText().toString());

              radioId = objRadioGroup.getCheckedRadioButtonId();
              objRadioButton = findViewById(radioId);

              objectMap.put("no_of_login", 0);
              objectMap.put("gender", objRadioButton.getText().toString());

              objectMap.put("no_of_image_status", 0);
              objectMap.put("no_of_text_status", 0);

              objectMap.put("user_location", "NA");
              objectMap.put("user_des", "NA");
              objectMap.put("user_purpose", "NA");

              objFirebaseFireStore.collection("UserProfileData")
                  .document(userEmailET.getText().toString())
                  .set(objectMap)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      Toast.makeText(RegisterPage.this, "User is registered", Toast.LENGTH_SHORT)
                          .show();
                      if (objFirebaseAuth.getCurrentUser() != null) {
                        objFirebaseAuth.signOut();
                      }

                      startActivity(new Intent(RegisterPage.this, LoginPage.class));
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Toast.makeText(RegisterPage.this,
                          "Fails to create user and upload data:" + e.getMessage(),
                          Toast.LENGTH_SHORT).show();
                    }
                  });
            } else if (!task.isSuccessful()) {
              Toast.makeText(RegisterPage.this, task.getException().toString(), Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });
      } else {
        Toast.makeText(this, "Please choose a profile image", Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      Toast.makeText(this, "Register Page" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private String getExtension(Uri uri) {
    try {

      ContentResolver objContentResolver = getContentResolver();
      MimeTypeMap objMimeTypeMap = MimeTypeMap.getSingleton();

      return objMimeTypeMap.getExtensionFromMimeType(objContentResolver.getType(uri));

    } catch (Exception e) {
      Toast.makeText(this, "RegisterPage" + e.getMessage(), Toast.LENGTH_SHORT).show();
      return null;
    }
  }

  private void chooseDOB() {
    try {
      Calendar objectCalendar = Calendar.getInstance();
      int year = objectCalendar.get(Calendar.YEAR);
      int month = objectCalendar.get(Calendar.MONTH);
      int day = objectCalendar.get(Calendar.DAY_OF_MONTH);

      objectOnDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
          month += 1;
          userBirthday.setText(dayOfMonth + "/" + month + "/" + year);
        }
      };

      DatePickerDialog objectDatePickerDialog = new DatePickerDialog(this,
          android.R.style.Theme_Material_Dialog, objectOnDateListener,
          year, month, day);

      objectDatePickerDialog.getWindow()
          .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      objectDatePickerDialog.show();
      

      /*

      objectOnDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
          month += 1;
          userBirthday.setText(dayOfMonth + "/" + month + "/" + year);
        }
      };

       */

    } catch (Exception e) {
      Toast.makeText(this, "Registration Page" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

  }

  private void attachJavaToXMLObject() {
    try {
      profileIV = findViewById(R.id.avatar_sign_up);
      userEmailET = findViewById(R.id.textEmailInput);
      userNicknameET = findViewById(R.id.textNicknameInput);
      userPasswordET = findViewById(R.id.textPasswordInput);
      userBirthday = findViewById(R.id.textBirthdayInput);

      registerBtn = findViewById(R.id.button_finish_signup);
      objRadioGroup = findViewById(R.id.objRadioGroup);

      profileIV.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          chooseImageFromMobileGallery();
        }
      });

      userBirthday.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          chooseDOB();
        }
      });


    } catch (Exception e) {
      Toast.makeText(this, "Register Page" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void chooseImageFromMobileGallery() {
    try {
      openMobileGallery();
    } catch (Exception e) {
      Toast.makeText(this, "Register Page" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void openMobileGallery() {
    try {
      Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
      galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

      startActivityForResult(galleryIntent, REQUEST_CODE);
    } catch (Exception e) {
      Toast.makeText(this, "Register Page" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data.getData() != null && data != null) {
      profileImageURL = data.getData();
      profileIV.setImageURI(profileImageURL);
    } else {
      Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
    }
  }
}
