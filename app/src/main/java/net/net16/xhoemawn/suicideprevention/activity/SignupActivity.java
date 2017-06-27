package net.net16.xhoemawn.suicideprevention.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.tools.UserType;

import es.dmoral.toasty.Toasty;

public class SignupActivity extends SuperActivity implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User newUser;
    private Button signUp;
    private Button cancel;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private ProgressDialog progressDialog;
    private int USEREXISTS = 0;
    private AlertDialog alertDialog ;
    RadioGroup radioGroup ;
    private PhoneAuthProvider phoneAuthProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");

        alertDialog= new AlertDialog.Builder(SignupActivity.this).create();
        newUser = new User();

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setCancelable(false);

        cancel = (Button) findViewById(R.id.cancelButton);
        signUp = (Button) findViewById(R.id.signupButton);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emailAddress);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        password.setTransformationMethod(new PasswordTransformationMethod());
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
        name.requestFocus();
        cancel.setOnClickListener(this);
        signUp.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cancelButton:
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.signupButton:
                if(USEREXISTS==1){
                    USEREXISTS = 0;

                    Toasty.error(SignupActivity.this,"User is already registered").show();
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    email.setError("Not email");
                    email.requestFocus();
                } else if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    password.setError("Passwords do not match");
                    password.requestFocus();

                }
                else if(password.getText().toString().length()<7){
                    password.setError("length must be greater than 7");
                    password.requestFocus();

                }
                else if(radioGroup.getCheckedRadioButtonId()==-1){
                    Toasty.error(SignupActivity.this,"Select Your Role.").show();
                }
                else{
                    newUser.setEmail(email.getText().toString());
                    newUser.setName(name.getText().toString());
                    newUser.setPassword(password.getText().toString());
                    newUser.setUserType(UserType.VICTIM);
                    newUser.setAvailable(true);
                    RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                    Integer userType = UserType.VICTIM;
                    switch (radioButton.getId()){
                        case R.id.radioButton3: userType = UserType.HELPER;
                            break;
                        case R.id.radioButton : userType = UserType.ADMIN;
                            break;
                    }
                    newUser.setUserType(userType);
                    progressDialog.show();
                    progressDialog.setMessage("Just a sec...");
                    signUpUser(newUser);


                }
                break;
        }
    }


    public void signUpUser(final User user){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    databaseReference.child(task.getResult().getUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                               // startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                                finish();
                            }
                        }
                    });
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    USEREXISTS = 1;
                    signUp.performClick();
                }
                else{
                    alertDialog.setMessage("Sorry, Unable to create Account.");
                    alertDialog.show();
                    alertDialog.setCancelable(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            name.requestFocus();
                        }
                    },3000);
                }
                progressDialog.dismiss();
            }
        });
    }
}
