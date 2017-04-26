package net.net16.xhoemawn.suicideprevention;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.net16.xhoemawn.suicideprevention.database.FetchDatabase;


public class LoginActivity extends AppCompatActivity {
    private boolean isLoggedIn = false;
    private  FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser =  null ;
    ProgressDialog progressDialog ;
    private Intent intent;
    private EditText editText;
    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);*/
        progressDialog = new ProgressDialog(LoginActivity.this);
      //  startActivity(intent);
         signIn = (Button)findViewById(R.id.button);
         signUp = (Button) findViewById(R.id.button2);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = (EditText)findViewById(R.id.editText);
                EditText editText1 = (EditText) findViewById(R.id.editText2);

                final String USERNAME = editText.getText().toString();
                final String PASSWORD = editText1.getText().toString();
                //  intent.putExtra("USERNAME",USERNAME);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(),0);
                if(!Patterns.EMAIL_ADDRESS.matcher(USERNAME).matches()){
                    editText.requestFocus();
                    editText.setError("Not a Email!");
                }

                else if(USERNAME.equals("")){
                    editText.requestFocus();
                    editText.setError("Empty Field");
                }
                else if(PASSWORD.equals("")){
                    editText.requestFocus();
                    editText1.setError("Empty Field");
                }
                else{
                    checkLogin(USERNAME,PASSWORD);
                }
                }
        });
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser =  firebaseAuth.getCurrentUser();
                if(firebaseUser!=null){
                    Log.d("SAD","ASDASD");
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                    progressDialog.dismiss();
                    intent.putExtra("USERNAME", "WTF");
                    startActivity(intent);
                    finish();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();


    }
    public void checkLogin(String email,String password){
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging in..");
        if(firebaseUser==null) {
            Log.d("SAD",email+"    "+" "+password);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    System.out.println(task.isSuccessful()+"");
                    if (task.isSuccessful()) {

                        Snackbar.make(findViewById(R.id.button), "Logging in", Snackbar.LENGTH_LONG).show();

                        isLoggedIn = true;
                    } else {
                        Log.d("SAD",task.toString());
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
                        },1000);

                    }
                }
            });

        }
    }



}
