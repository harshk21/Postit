package com.hk210.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextInputLayout t1, t2, t3, t4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        t1 = findViewById(R.id.username);
        t2 = findViewById(R.id.emailSign);
        t3 = findViewById(R.id.passwordsign);
        t4 = findViewById(R.id.passwordConfirm);
        mAuth = FirebaseAuth.getInstance();

    }

    public void loginIntent(View view) {
        Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(login);
        finish();

    }

    public void updateUI() {
        Intent profile = new Intent(SignUpActivity.this, ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    public void signup(View view) {

        String email = t2.getEditText().getText().toString();
        String password = t3.getEditText().getText().toString();
        String pwdConfirm = t4.getEditText().getText().toString();

        if (email.isEmpty() && password.isEmpty() && pwdConfirm.isEmpty()) {
            Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(pwdConfirm)) {
            Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI();
                            } else {

                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
}
