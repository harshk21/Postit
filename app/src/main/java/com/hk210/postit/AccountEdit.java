package com.hk210.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountEdit extends AppCompatActivity {

    private CircleImageView imageView2;
    private Button b12;
    public Uri imageUri2 = null;
    private String myUri2 = "";
    private TextInputLayout t12;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;
    private boolean ischanged = false;
    private DatabaseReference dref;
    private StorageTask uplStorageTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
        getSupportActionBar().setTitle("Edit Account");
        imageView2 = findViewById(R.id.profile_image2);
        b12 = findViewById(R.id.nextProfile);
        t12 = findViewById(R.id.name);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        userId = firebaseAuth.getCurrentUser().getUid();

        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        t12.getEditText().setText(name);
                        RequestOptions placeholder = new RequestOptions();
                        placeholder.placeholder(R.drawable.ab);
                        Glide.with(AccountEdit.this).setDefaultRequestOptions(placeholder).load(image).into(imageView2);


                    }
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(AccountEdit.this, error+"", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void onClickImage(View v) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountEdit.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri2 = result.getUri();
                imageView2.setImageURI(imageUri2);
                ischanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public void onClick(View view) {
        final String userName = t12.getEditText().getText().toString();
        if (ischanged){
            if (!TextUtils.isEmpty(userName) && imageUri2 != null) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                final StorageReference fileRef = mStorageRef.child("profile_images").child(userId + ".jpg");
                uplStorageTask = fileRef.putFile(imageUri2);
                uplStorageTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return fileRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            storeData(task, userName);

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(AccountEdit.this, error + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
    }
        else {
            storeData(null,userName);
        }
    }

    private void storeData(@NonNull Task <Uri> task, String userName) {
        Uri downloadUri;
        if(task != null){
            downloadUri = task.getResult();
            myUri2 = downloadUri.toString();}
        else{
            downloadUri = imageUri2;

        }
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("image",myUri2);
        db.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AccountEdit.this, "Updated Successful", Toast.LENGTH_SHORT).show();

                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(AccountEdit.this, error+"", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}