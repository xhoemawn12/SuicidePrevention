package net.net16.xhoemawn.suicideprevention.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.UserListAdapter;
import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.tools.UserType;

import java.util.LinkedHashMap;
import java.util.Objects;

public class UserListFragment extends android.support.v4.app.Fragment {
    private RecyclerView recyclerView;
    private UserListAdapter userAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private LinkedHashMap<String, User> userLinkedHashMap;
    private User currentUser = null;

    public static UserListFragment getInstance() {
        return new UserListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_fragment, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.userRecycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");
        userLinkedHashMap = new LinkedHashMap<String, User>();
        userAdapter = new UserListAdapter(userLinkedHashMap);
        recyclerView.setAdapter(userAdapter);

        final TextView textView = (TextView) v.findViewById(R.id.typeUser);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if (!Objects.equals(currentUser.getUserType(), UserType.HELPER)) {
                    textView.setText("Available Helpers");
                } else
                    textView.setText("Victims Seeking Help");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        final TextView textView1 = (TextView) v.findViewById(R.id.messageAlert);

        textView1.setVisibility(View.GONE);
        databaseReference.orderByChild("available").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userLinkedHashMap.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    if (currentUser != null && user!=null &&user.getUserType()!=null
                            && !currentUser.getUserType().equals(dataSnapshot1.getValue(User.class).getUserType()))
                        if (!user.getUserType().equals(UserType.ADMIN))
                            userLinkedHashMap.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(User.class));
                    if (userLinkedHashMap.size() == 0) {
                        textView1.setVisibility(View.VISIBLE);

                    } else {
                        textView1.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);
                }
                userAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }
}
