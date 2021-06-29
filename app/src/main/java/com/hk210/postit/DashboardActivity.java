package com.hk210.postit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton b1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mRef;
    public String currentUserID;

    PostAdapter adapter;
    private RecyclerView postView;
    ArrayList<PostModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("New Posts");
        postView = findViewById(R.id.postListView);
        postView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new PostAdapter(dataList);
        postView.setAdapter(adapter);

        b1 = findViewById(R.id.floatingActionButton);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

       if(mAuth.getCurrentUser() != null){

           mFirestore.collection("Posts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot d:list){
                            PostModel obj = d.toObject(PostModel.class);
                            dataList.add(obj);
                        }
                        //updateadapter

                        adapter.notifyDataSetChanged();

                    }
                });}

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    System.out.println("User logged in");
                    checkForProfileData();

                }
                else{
                    System.out.println("User not logged in");
                }
            }
        };

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, NewPostActivity.class));
            }
        });


    }

    private void retrieveData() {

    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }
    public void accountSetup(){
        startActivity(new Intent(DashboardActivity.this, AccountEdit.class));
    }
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);





    }
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    public void checkForProfileData(){
        currentUserID  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirestore.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().exists()){
                        startActivity(new Intent(DashboardActivity.this,ProfileActivity.class));
                        finish();
                    }
                }
                else{
                    Toast.makeText(DashboardActivity.this, "Not Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.accountSet:
                accountSetup();
                return true;
            case R.id.logOff:
                logout();
                finish();
                return true;
            default:
                return false;
        }

    }


}