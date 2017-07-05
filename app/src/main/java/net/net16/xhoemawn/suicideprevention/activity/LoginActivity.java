package net.net16.xhoemawn.suicideprevention.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.model.User;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends SuperActivity implements View.OnClickListener {
    private boolean isLoggedIn = false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = null;
    private Intent intent;
    private EditText editText;
    private Button signIn;
    private Button signUp;
    EditText editText1;
    private ProgressBar progressBar;
    private Integer userType ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toasty.Config.getInstance().apply();
        editText1 = (EditText) findViewById(R.id.editText2);
        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.GONE);
        signIn = (Button) findViewById(R.id.button);
        signUp = (Button) findViewById(R.id.button2);
        signIn.setOnClickListener(this);
        editText1.setTransformationMethod(new PasswordTransformationMethod());
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Toasty.info(LoginActivity.this,"Please Wait..").show();
                    signIn.setClickable(false);
                    signUp.setClickable(false);
                    FirebaseDatabase.getInstance().getReference("User/"+firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            try {
                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                if(user!=null && user.getUserType()!=null) {
                                    userType = user.getUserType();
                                    intent.putExtra("USERTYPE", userType);
                                    intent.putExtra("USERNAME", firebaseUser.getEmail());
                                    progressBar.setVisibility(View.GONE);
                                    Toasty.success(LoginActivity.this,"Success..").show();
                                    signIn.setClickable(true);
                                    signUp.setClickable(true);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            catch (NullPointerException nul){
                                userType = null;
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    firebaseAuth.removeAuthStateListener(this);
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
                } catch (NullPointerException nullPointer) {
                    Log.d("", "No Active window");
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(USERNAME).matches()) {
                    editText.requestFocus();
                    editText.setError("");
                    Toasty.error(LoginActivity.this, "Not an email.").show();
                } else if (USERNAME.equals("")) {
                    editText.requestFocus();
                    editText.setError("Empty Field");
                    Toasty.error(LoginActivity.this, "Username cannot be empty.").show();
                } else if (PASSWORD.equals("")) {
                    editText1.requestFocus();
                    editText1.setError("");
                    Toasty.error(LoginActivity.this, "Password empty.").show();
                } else {
                    checkLogin(USERNAME, PASSWORD);
                }
                break;
            case R.id.button2:
                final Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                startActivity(intent);
                finish();
                break;
        }
    }


    public void checkLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        Toasty.info(LoginActivity.this,"Logging you in..").show();
        signIn.setClickable(false);
        signUp.setClickable(false);
        if (firebaseUser == null) {
            Log.d("SAD", email + "    " + " " + password);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    System.out.println(task.isSuccessful() + "");
                    if (task.isSuccessful()) {
                        isLoggedIn = true;
                        
                    } else {
                        Log.d("SAD", task.toString());
                        progressBar.setVisibility(View.GONE);
                        Toasty.error(LoginActivity.this,"Incorrect Username/Password..").show();
                        signIn.setClickable(true);
                        signUp.setClickable(true);
                        editText.requestFocus();

                    }
                }
            });

        }
    }


}
