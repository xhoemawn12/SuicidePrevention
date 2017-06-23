package net.net16.xhoemawn.suicideprevention.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.Model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.UserListAdapter;

import java.util.LinkedHashMap;

public class UserListFragment extends android.support.v4.app.Fragment {
    private RecyclerView recyclerView;
    private UserListAdapter userAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private LinkedHashMap<String, User> userLinkedHashMap;

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
        databaseReference.orderByChild("available").equalTo(true).addValueEventListener(new ValueEventListener() {
            User currentUser = null;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userLinkedHashMap.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    System.out.println("EMAIL|"+user.getEmail());
                    FirebaseDatabase.getInstance().getReference("User/").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            currentUser = dataSnapshot.getValue(User.class);
                            System.out.print("CURRENT"+currentUser.getUserType());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    if (currentUser != null && !currentUser.getUserType().equals(dataSnapshot1.getValue(User.class).getUserType()))
                        userLinkedHashMap.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(User.class));
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
