package com.hk210.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 100 ;
    private TextInputLayout inTitile,inDes;
    private Button postIt;
    private ImageView imagePostView;

    private String currentUserID;

    public Uri imagePostUri = null;
    private String myPostUri = "";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageTask uplStorageTask;
    private StorageReference mStorageRef;
    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        getSupportActionBar().setTitle("Add Post");

        inTitile = findViewById(R.id.title_Post);
        inDes = findViewById(R.id.des_Post);
        postIt = findViewById(R.id.postButton);
        imagePostView = findViewById(R.id.imagePost);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();


    }

    public void onPostImageClick(View v) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(800,600)
                .setAspectRatio(2,1)
                .start(NewPostActivity.this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imagePostUri = result.getUri();
                imagePostView.setImageURI(imagePostUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    public void onClickUpload(View view) {
        final String title = inTitile.getEditText().getText().toString();
        final String description = inDes.getEditText().getText().toString();
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)&& imagePostUri != null){
            String userId = mAuth.getCurrentUser().getUid();
            String randonName = UUID.randomUUID().toString();
            final StorageReference fileRef = mStorageRef.child("posts").child(randonName +".jpg");
            uplStorageTask = fileRef.putFile(imagePostUri);
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
                    Uri downloadUri = task.getResult();
                    if(task.isSuccessful()){

                        File filepath = new File(imagePostUri.getPath());

                        try {
                            compressedImageBitmap = new Compressor(NewPostActivity.this)
                                    .setMaxHeight(200)
                                    .setMaxWidth(400)
                                    .setQuality(5)
                                    .compressToBitmap(filepath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();
                        UploadTask uploadTask = mStorageRef.child("posts/thumbs").child(randonName+".jpg").putBytes(thumbData);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                myPostUri = downloadUri.toString();
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("title", title);
                                userMap.put("description",description);
                                userMap.put("post",myPostUri);
                                userMap.put("timestamp", FieldValue.serverTimestamp());
                                userMap.put("userid",userId);
                                mFirestore.collection("Posts").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()){
                                            updateUI();
                                            Toast.makeText(NewPostActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                                        }
                                        else{

                                        }

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(NewPostActivity.this, error+"", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }

    private void updateUI() {
        startActivity(new Intent(NewPostActivity.this, DashboardActivity.class));
        finish();
    }


}