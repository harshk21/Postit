package com.hk210.postit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


public class ProfileActivity extends AppCompatActivity {
    private CircleImageView imageView;
    private Button b1;
    public Uri imageUri = null;
    private String myUri = "";
    private TextInputLayout t1;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DatabaseReference dref;
    private StorageTask uplStorageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile Setup");
        imageView = findViewById(R.id.profile_image);
        b1 = findViewById(R.id.nextProfile);
        t1 = findViewById(R.id.name);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();



    }

    public void onClickImage(View v) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfileActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void onClick(View view) {
        final String userName = t1.getEditText().getText().toString();
        if(!TextUtils.isEmpty(userName) && imageUri != null){
           String userId = firebaseAuth.getCurrentUser().getUid();
          final StorageReference fileRef = mStorageRef.child("profile_images").child(userId +".jpg");
          uplStorageTask = fileRef.putFile(imageUri);
          uplStorageTask.continueWithTask(new Continuation() {
              @Override
              public Object then(@NonNull Task task) throws Exception {
                  if(!task.isSuccessful()){
                      throw task.getException();
                  }

                  return fileRef.getDownloadUrl();
              }
          }).addOnCompleteListener(new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task <Uri> task) {
                  if(task.isSuccessful()){
                      Uri downloadUri = task.getResult();
                      myUri = downloadUri.toString();
                      HashMap<String, Object> userMap = new HashMap<>();
                      userMap.put("name", userName);
                      userMap.put("image",myUri);
                      db.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(ProfileActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                  updateUI();

                              }
                              else{
                                  String error = task.getException().getMessage();
                                  Toast.makeText(ProfileActivity.this, error+"", Toast.LENGTH_SHORT).show();
                              }

                          }
                      });
                  }
                  else{
                      String error = task.getException().getMessage();
                      Toast.makeText(ProfileActivity.this, error+"", Toast.LENGTH_SHORT).show();
                  }
              }
          });


        }

    }
    public void updateUI() {
        Intent dashboard = new Intent(ProfileActivity.this, DashboardActivity.class);
        startActivity(dashboard);
        finish();
    }


}