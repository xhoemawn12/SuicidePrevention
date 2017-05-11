package net.net16.xhoemawn.suicideprevention.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.xhoemawn.suicideprevention.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.textView3).setAnimation(animate(0));
        findViewById(R.id.textView).setAnimation(animate(0));
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch(DatabaseException database){
            FirebaseDatabase.getInstance();
        }

            new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
    public static Animation animate(int CASE)
    {

        Animation anim = new ScaleAnimation(0f,5f,0f,5f);
        switch (CASE){
            case 0:
                anim = new AlphaAnimation(0,1);
                break;
            case 1:
                anim = new RotateAnimation(1,10);
                break;
        }
        anim.setDuration(5000);
        anim.setInterpolator(new DecelerateInterpolator());

        return anim;

    }
}
