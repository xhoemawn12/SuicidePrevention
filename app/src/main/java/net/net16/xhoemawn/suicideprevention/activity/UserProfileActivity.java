package net.net16.xhoemawn.suicideprevention.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.fragment.UserFragment;

public class UserProfileActivity extends SuperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Fragment fragment = UserFragment.newInstance(getIntent().getExtras().getString("USERID"));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.constraintUserProfile, fragment);
        fragmentTransaction.commit();

    }
}
