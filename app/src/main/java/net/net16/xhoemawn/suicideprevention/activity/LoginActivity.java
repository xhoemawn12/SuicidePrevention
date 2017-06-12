package net.net16.xhoemawn.suicideprevention.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import net.net16.xhoemawn.suicideprevention.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isLoggedIn = false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = null;
    ProgressDialog progressDialog;
    private Intent intent;
    private EditText editText;
    private Button signIn;
    private Button signUp;
    EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText1 = (EditText) findViewById(R.id.editText2);
        progressDialog = new ProgressDialog(LoginActivity.this);
        signIn = (Button) findViewById(R.id.button);
        signUp = (Button) findViewById(R.id.button2);
        signIn.setOnClickListener(this);
        editText1.setTransformationMethod(new PasswordTransformationMethod());

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.d("SAD", "ASDASD");
                    intent = new Intent(LoginActivity.this, HomeActivity.class);

                    intent.putExtra("USERNAME", firebaseUser.getEmail());
                    startActivity(intent);
                    firebaseAuth.removeAuthStateListener(this);
                    finish();
                }
            }
        });

        signUp.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                editText = (EditText) findViewById(R.id.editText);

                final String USERNAME = editText.getText().toString();
                final String PASSWORD = editText1.getText().toString();
                //  intent.putExtra("USERNAME",USERNAME);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    try {
                        inputMethodManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getApplicationWindowToken(), 0);
                    }
                    catch(NullPointerException nullPointer){
                        Log.d("","No Active window");
                    }
                        if (!Patterns.EMAIL_ADDRESS.matcher(USERNAME).matches()) {
                    editText.requestFocus();
                    editText.setError("Not a Email!");
                } else if (USERNAME.equals("")) {
                    editText.requestFocus();
                    editText.setError("Empty Field");
                } else if (PASSWORD.equals("")) {
                    editText.requestFocus();
                    editText1.setError("Empty Field");
                } else {
                    checkLogin(USERNAME, PASSWORD);
                }
                break;
            case R.id.button2:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
                break;
        }
    }


    public void checkLogin(String email, String password) {
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in..");
        if (firebaseUser == null) {
            Log.d("SAD", email + "    " + " " + password);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    System.out.println(task.isSuccessful() + "");
                    if (task.isSuccessful()) {
                        Snackbar.make(findViewById(R.id.button), "Logging in", Snackbar.LENGTH_LONG).show();
                        isLoggedIn = true;
                        progressDialog.dismiss();
                    } else {
                        Log.d("SAD", task.toString());
                        progressDialog.dismiss();
                        final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("ERROR");
                        alertDialog.setMessage("Sorry, Wrong Email/Password");
                        alertDialog.show();
                        alertDialog.setCancelable(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                editText.requestFocus();
                            }
                        }, 3000);

                    }
                }
            });

        }
    }


}
