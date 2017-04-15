package net.net16.xhoemawn.suicideprevention;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Login");

        setSupportActionBar(toolbar);
        Snackbar snk = Snackbar.make(findViewById(R.id.button),"Confirm",Snackbar.LENGTH_LONG);
        snk.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Hello, WOrld",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        snk.show();
        Button signIn = (Button)findViewById(R.id.button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                final String USERNAME = editText.getText().toString();
                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("USERNAME",USERNAME);

                startActivity(intent);
            }
        });
    }



}
