package net.net16.xhoemawn.suicideprevention.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.PostAdapter;
import net.net16.xhoemawn.suicideprevention.model.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class PostFragment extends android.support.v4.app.Fragment implements  View.OnClickListener, ValueEventListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private LinkedHashMap<String,Post> postsList;
    private Post savePost;
    private EditText postBody;
    private ImageView imageView;
    private String uid;
    private Button savePostButton;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    public static PostFragment newInstance(String uid) {
        PostFragment postFragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("UID", uid);
        postFragment.setArguments(bundle);
        return postFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_fragment,container,false);
        uid = getArguments().getString("UID");
        firebaseDatabase = FirebaseDatabase.getInstance();
        postsList = new LinkedHashMap<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        postBody = (EditText) v.findViewById(R.id.postBody);
        savePostButton = (Button) v.findViewById(R.id.postNewButton);
        savePostButton.setOnClickListener(this);
        recyclerView = (RecyclerView)v.findViewById(R.id.postRecycler);

        postAdapter= new PostAdapter(postsList);
        recyclerView.setAdapter(postAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase.getReference("Post/").addValueEventListener(this);
        recyclerView.requestFocus();
        return v;
    }

    public void saveNewPost(Post post){
        if(firebaseUser!=null){
            post.setPostedBy(firebaseUser.getUid());
            post.setTimeStamp(Calendar.getInstance().getTimeInMillis()+"");

        }

        firebaseDatabase.getReference("Post/").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.success(getActivity(),"Success.").show();
            }
        });
    }
    public void commendToPost(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postNewButton:
                savePost = new Post();
                savePost.setPostBody(postBody.getText().toString());
                postBody.setText("");
                postBody.setHint("Write Something Amazing here.");
                saveNewPost(savePost);
                break;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
            postsList.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Post.class));
        }
        postAdapter.notifyDataSetChanged();;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
