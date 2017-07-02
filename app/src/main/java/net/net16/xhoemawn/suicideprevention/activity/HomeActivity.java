package net.net16.xhoemawn.suicideprevention.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.fragment.ChatFragment;
import net.net16.xhoemawn.suicideprevention.fragment.PostFragment;
import net.net16.xhoemawn.suicideprevention.fragment.UserFragment;
import net.net16.xhoemawn.suicideprevention.fragment.UserListFragment;
import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.tools.UserType;

import java.util.Objects;

public class HomeActivity extends SuperActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseUser firebaseUser;
    private Integer userType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userType = getIntent().getExtras().getInt("USERTYPE");
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("USERNAME"));*/
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        try {
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_black_24dp);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_group_black_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_person_black_24dp);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_chrome_reader_mode_black_24dp);
        }
        catch (NullPointerException nul){
            Log.d("",nul.getLocalizedMessage());
        }
        }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(!Objects.equals(userType, UserType.ADMIN))
            switch (position) {
                case 0:
                    return UserListFragment.getInstance();
                case 1:
                    return ChatFragment.newInstance(firebaseUser.getUid());
                case 2:
                    return UserFragment.newInstance(firebaseUser.getUid());
                case 3:
                    return PostFragment.newInstance(firebaseUser.getUid());
            }
            else
                    return UserFragment.newInstance(firebaseUser.getUid());
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.

            if(!Objects.equals(userType, UserType.ADMIN))
                return 4;
            else
                return 1;
        }

    }
}
