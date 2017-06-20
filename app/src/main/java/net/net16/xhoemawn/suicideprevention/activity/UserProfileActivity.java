package net.net16.xhoemawn.suicideprevention.activity;


import android.support.constraint.ConstraintLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.fragment.UserFragment;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Fragment fragment = UserFragment.newInstance(getIntent().getExtras().getString("USERID"));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.constraintUserProfile, fragment);
        fragmentTransaction.commit();

    }
}
