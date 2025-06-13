package com.example.familymart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Button btnLogin = findViewById(R.id.loginBtn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });

        TextView tvSwitchToRegister = findViewById(R.id.registerBtn);
        tvSwitchToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });
    }


    private void authenticateUser() {
        EditText etLoginEmail = findViewById(R.id.email);
        EditText etLoginPassword = findViewById(R.id.password);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login success",
                                    Toast.LENGTH_SHORT).show();
                            if(currentuser.equals("UaBWD8kHUfZtiuk1RZzR1Czr6js2"))
                                showKeeperPage();
                            else
                                showMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email or password error, please re-enter",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showMainActivity() {
        Intent intent = new Intent(LoginActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    private void showKeeperPage() {
        Intent intent = new Intent(LoginActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    private void switchToRegister() {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
        finish();
    }





}


