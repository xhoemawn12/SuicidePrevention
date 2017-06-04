package net.net16.xhoemawn.suicideprevention.activity;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.xhoemawn.suicideprevention.Model.Chat;
import net.net16.xhoemawn.suicideprevention.fragment.PlaceHolderFragment;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.fragment.ChatFragment;
import net.net16.xhoemawn.suicideprevention.fragment.UserFragment;
import net.net16.xhoemawn.suicideprevention.fragment.UserListFragment;

public class HomeActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("USERNAME"));*/
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_chat_black_24dp);
        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_group_black_48dp);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_person_black_48dp);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

             switch (position){
                case 0 :
                    return UserListFragment.getInstance();
                case 1:
                    return  ChatFragment.newInstance(firebaseUser.getUid());
                case 2:
                    return   UserFragment.newInstance(firebaseUser.getUid());
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }
}
