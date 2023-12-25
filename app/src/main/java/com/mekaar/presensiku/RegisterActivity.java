package com.mekaar.presensiku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mRegisterbtn;
    TextView mHaveaccount;


    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Buat Akun");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mRegisterbtn = findViewById(R.id.registrasiBtn);
        mHaveaccount = findViewById(R.id.have_account);


        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Users..");

        mRegisterbtn.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
                mEmail.setError("Invalid Email");
                mEmail.setFocusable(true);
            } else if (password.length()<6) {
                mPassword.setError("Password kurang dari 6 karakter");
                mPassword.setFocusable(true);
            } else {
                registerUser(email, password);
            }

        });

        //handle login text view listerner
        mHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


    }

    private void registerUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (task) -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        progressDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();
                        //get user email and uid from auth
                        String email1 = user.getEmail();
                        String uid = user.getUid();
                        //when user registed store user info to realtime database too
                        //using HasMap
                        HashMap<Object, String> hashMap = new HashMap<>();
                        //put into HasMap
                        hashMap.put("nik", "");
                        hashMap.put("email", email1);
                        hashMap.put("uid", uid);
                        hashMap.put("name", ""); //will add later in edit profile
                        hashMap.put("periode", "");
                        hashMap.put("phone", ""); //will add later in edit profile
                        hashMap.put("image", ""); //will add later in edit profile
                        //firebase database instance
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://presensiku-520ee-default-rtdb.asia-southeast1.firebasedatabase.app/");
                        //path to store user data named "Users"
                        DatabaseReference reference = database.getReference("Users");
                        //put data within hasmap in database
                        reference.child(uid).setValue(hashMap);


                        Toast.makeText(RegisterActivity.this, "Registrasi..\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registrasi Gagal!",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go previous activity
        return super.onSupportNavigateUp();
    }
}