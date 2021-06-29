package com.hk210.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.*;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;




public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView main, secondary,newUser;
    private TextInputLayout t1, t2;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private Button gSignIn;
    private Button forgetpass,signin,passresetBack,signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        t1 = findViewById(R.id.email);
        t2 = findViewById(R.id.password);
        gSignIn = findViewById(R.id.gSignInButton);
        forgetpass =  findViewById(R.id.forgotPassword);
        main =  findViewById(R.id.maintext);
        secondary =  findViewById(R.id.secondtext);
        newUser =  findViewById(R.id.newUsertext);
        signin =  findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUpButton);
        passresetBack =  findViewById(R.id.forgotPasswordback);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("997916229127-71lkf7ih19e7uj6gvqgg3srtlj5rpkjs.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        
        gSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetpass.setText("Send Link");
                main.setText("Reset your password");
                secondary.setText("No need to worry just enter your email");
                newUser.setVisibility(View.INVISIBLE);
                t2.setVisibility(View.INVISIBLE);
                signin.setVisibility(View.INVISIBLE);
                forgetpass.setVisibility(View.VISIBLE);
                passresetBack.setVisibility(View.VISIBLE);
                signUp.setVisibility(View.INVISIBLE);
                gSignIn.setVisibility(View.INVISIBLE);


                final String email = t1.getEditText().getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Cannot be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        passresetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setText(R.string.hello_there_welcome_back);
                secondary.setText(R.string.sign_in);
                t2.setVisibility(View.VISIBLE);
                signin.setVisibility(View.VISIBLE);
                forgetpass.setVisibility(View.VISIBLE);
                passresetBack.setVisibility(View.INVISIBLE);
                signUp.setVisibility(View.VISIBLE);
                gSignIn.setVisibility(View.VISIBLE);
                newUser.setVisibility(View.VISIBLE);
                forgetpass.setText(R.string.forget_password);

            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = t1.getEditText().getText().toString();
                String password = t2.getEditText().getText().toString();

                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Cannot be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        updateUI(user);
                                    } else {

                                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this,"Welcome back "+ account.getEmail(), Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, account.getId(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            finish();
        } catch (ApiException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/
    private void firebaseAuthWithGoogle(String idToken) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void signUpintent(View view) {
        Intent signUp = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(signUp);
    }

    public void login(View view) {


    }

    private void updateUI(FirebaseUser user) {
        Intent dashboard = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(dashboard);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI(user);
        }
    }
}
