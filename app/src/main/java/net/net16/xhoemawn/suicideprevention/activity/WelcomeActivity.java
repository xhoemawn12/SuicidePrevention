package net.net16.xhoemawn.suicideprevention.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;

public class WelcomeActivity extends SuperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_welcome);
       // findViewById(R.id.textView3).setAnimation(animate(0));
       // findViewById(R.id.textView).setAnimation(animate(0));
        YoYoAnimation(findViewById(R.id.textView),Techniques.BounceInLeft);
        YoYoAnimation(findViewById(R.id.textView3),Techniques.BounceInRight);
        YoYoAnimation(findViewById(R.id.textView4),Techniques.BounceInDown);
        YoYoAnimation(findViewById(R.id.textView5),Techniques.BounceInUp);
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
    public static void YoYoAnimation(View v, Techniques techniques){
        YoYo.with(techniques)
                .duration(3000)

                .playOn(v);
    }
}
