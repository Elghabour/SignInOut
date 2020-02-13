package com.example.signuplogintask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupBioActivity extends AppCompatActivity {

    private EditText bio;
    private Button skipButton;
    private Button nextButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_bio);

        bio = findViewById(R.id.editTextSignup_bio);
        skipButton = findViewById(R.id.signupBio_skipButton);
        nextButton = findViewById(R.id.signupBio_nextButton);

        bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0){
                    skipButton.setVisibility(View.INVISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupBioActivity.this , LoginActivity.class));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> addBio = new HashMap<>();
                addBio.put("bio", bio.getText().toString().trim());
                startActivity(new Intent(SignupBioActivity.this , LoginActivity.class));

                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(addBio)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("addBio", "Done");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("addBio" , e.toString());
                            }
                        });

                Toast.makeText(SignupBioActivity.this, "First verify your account then login", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
