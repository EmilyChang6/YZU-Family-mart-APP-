package com.example.familymart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    ImageView googleBtn;
    TextView username, password;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleBtn = findViewById(R.id.googleBtn);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        dbHelper = new DBHelper(this);
        dbHelper.insertUserData("Njabulo", "12", "njabuloshong1@gmail.com");
        dbHelper.insertUserData("Emily", "0208", "ifntstar0609@gmail.com");
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uAccount = username.getText().toString();
                String uPassword = password.getText().toString();
                if (uAccount.isEmpty() || uPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, (uAccount.isEmpty() ?"Account" :
                            "Password") + " is empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.getLogin(uAccount, uPassword)) {
                        Intent intent = new Intent(MainActivity.this,Home.class);
                        intent.putExtra("from", "login");
                        intent.putExtra("id", uAccount);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Account and password error, please re-enter", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void navigateToSecondActivity() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }


}