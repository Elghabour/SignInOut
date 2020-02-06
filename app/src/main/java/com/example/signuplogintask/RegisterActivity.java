package com.example.signuplogintask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText fName;
    private EditText lName;
    private Spinner countrySelect;
    private RadioGroup genderGroup;
    private EditText phone;
    private EditText email;
    private EditText confirmPassword;
    private EditText password;
    private TextView birthDate;
    private Button signUp;


    //keys
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String USER_GENDER = "gender";
    private static final String USER_COUNTRY = "country";
    private static final String USER_PHONE = "phone";
    private static final String BIRTH_DATE = "birth_date";
    private static final String VALID_PHONE = "^[+]?[0-9]{8,20}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";


    //firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fName = findViewById(R.id.signUpFirstName);
        lName = findViewById(R.id.signUpLastName);
        genderGroup = findViewById(R.id.radioGroup);
        countrySelect = findViewById(R.id.countrySpinner);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.signUpEmail);
        password = findViewById(R.id.signUpPassword);
        confirmPassword = findViewById(R.id.signUpConfirmPassword);
        signUp = findViewById(R.id.btnSignUp);
        birthDate = findViewById(R.id.show_dialog);

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //country
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        countrySelect.setAdapter(adapter);


        ////////////////////Sign up
        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_confirmPassword = confirmPassword.getText().toString();

                if(TextUtils.isEmpty(fName.getText().toString())){
                    fName.setError("Enter your First Name");
                } else if(TextUtils.isEmpty(lName.getText().toString())){
                    lName.setError("Enter your Last Name");
                } else if (birthDate.getText().toString().equals("Select Your Date Of Birth")){
                    Toast.makeText(RegisterActivity.this, "Select your date of birth", Toast.LENGTH_SHORT).show();
                } else if (genderGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this, "Select Your Gender", Toast.LENGTH_SHORT).show();
                } else if(! phone.getText().toString().matches(VALID_PHONE)) {
                    phone.setError("Phone is not valid");
                } else if (TextUtils.isEmpty(txt_email)){
                    email.setError("Enter your Email");
                    //Toast.makeText(RegisterActivity.this, "Empty Email or Password!", Toast.LENGTH_SHORT).show();
                } else if(! Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                    email.setError("Email is not valid");
                } else if(password.getText().toString().length() < 8 ){
                    if(TextUtils.isEmpty(txt_password)){
                        password.setError("Enter your Password");
                    }else{
                        Toast.makeText(RegisterActivity.this, "Too short password", Toast.LENGTH_SHORT).show();
                    }
                } else if(TextUtils.isEmpty(txt_confirmPassword)){
                        confirmPassword.setError("Enter your Confirm Password");
                } else if(! confirmPassword.getText().toString().equals(password.getText().toString())){
                    confirmPassword.setError("Password doesn't match");
                } else if (! isValidPassword(confirmPassword.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Please add at least 1 Alphabet," + "\n" +" 1 Number and 1 Special Character", Toast.LENGTH_LONG).show();
                } else {
                    signUpUser(txt_email , txt_password);
                    saveUserData();
                }
            }
        });//end of signUp.setOnClickListener


        //makes Egypt the default country
        ArrayAdapter<String> spinnerAdap = (ArrayAdapter<String>) countrySelect.getAdapter();
        int spinnerPosition = spinnerAdap.getPosition("Egypt");
        countrySelect.setSelection(spinnerPosition);

    }//end of onCreate


    //password validation
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }



    ////////Date of birth
    final Calendar c = Calendar.getInstance();
    private int dayNow = c.get(Calendar.DAY_OF_MONTH);
    private int monthNow = c.get(Calendar.MONTH);
    private int yearNow= c.get(Calendar.YEAR);

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, dayNow, monthNow, yearNow);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (year > yearNow || year >= yearNow &&  month > monthNow || year >= yearNow &&  month >= monthNow && dayOfMonth > dayNow) {
            Toast.makeText(RegisterActivity.this, "Date of Birth can't be from the future!", Toast.LENGTH_LONG).show();
        }
        String date =  dayOfMonth + "/" + month + 1 + "/" + year;
        birthDate.setText(date);
    }




    ///////Signing up
    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this , new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this , HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//end of signUpUser

    private void saveUserData() {
        int genderID = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(genderID);

        String firstName = fName.getText().toString();
        String lastName = lName.getText().toString();
        String userGender= selectedRadioButton.getText().toString();
        String userCountry = countrySelect.getSelectedItem().toString();
        String userPhone = phone.getText().toString();

        Map<String, Object> userData = new HashMap<>();

        userData.put(FIRST_NAME, firstName);
        userData.put(LAST_NAME, lastName);
        userData.put(USER_GENDER, userGender);
        userData.put(USER_COUNTRY, userCountry);
        userData.put(USER_PHONE, userPhone);
        userData.put(BIRTH_DATE, birthDate.getText().toString());

        db.collection("users").document().set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saveData", "Done");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("saveData", e.toString());
                    }
                });
    }//end of saveUserData







}
