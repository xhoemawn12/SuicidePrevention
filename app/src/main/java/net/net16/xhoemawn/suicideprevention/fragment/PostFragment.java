package net.net16.xhoemawn.suicideprevention.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.PostAdapter;
import net.net16.xhoemawn.suicideprevention.callbacks.ImageResult;
import net.net16.xhoemawn.suicideprevention.model.Post;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;

import es.dmoral.toasty.Toasty;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class PostFragment extends android.support.v4.app.Fragment implements View.OnClickListener, ValueEventListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private LinkedHashMap<String, Post> postsList;
    private Post savePost;
    private EditText postBody;
    private ImageView imageView;
    private String uid;
    private Button savePostButton;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ImageResult imageResult;
    private ProgressBar progressBar;
    private Uri imageURI;
    private ImageButton imageButton;


    public ImageResult getImageResult() {
        return imageResult;
    }

    public void setImageResult(ImageResult imageResult) {
        this.imageResult = imageResult;
    }

    public static PostFragment newInstance(String uid) {
        PostFragment postFragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("UID", uid);
        postFragment.setArguments(bundle);
        return postFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            imageResult.resultStatus(true, data, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImageIntent();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {

                    Toasty.warning(getActivity(), "Unable To Access Storage", 300).show();
                } else {
                    startImageIntent();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_fragment, container, false);
        uid = getArguments().getString("UID");
        firebaseDatabase = FirebaseDatabase.getInstance();
        postsList = new LinkedHashMap<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        postBody = (EditText) v.findViewById(R.id.postBody);
        savePostButton = (Button) v.findViewById(R.id.postNewButton);
        imageButton = (ImageButton) v.findViewById(R.id.imageButton2);
        savePostButton.setOnClickListener(this);
        recyclerView = (RecyclerView) v.findViewById(R.id.postRecycler);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);
        postAdapter = new PostAdapter(postsList);
        recyclerView.setAdapter(postAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase.getReference("Post/").addValueEventListener(this);
        recyclerView.requestFocus();
        imageButton.setOnClickListener(this);
        setImageResult(new ImageResult() {
            @NonNull
            @Override
            public void resultStatus(Boolean boo, Intent data, Integer type) {
                if (boo && type == 0) {
                    imageURI = data.getData();
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReference();
                        Toasty.info(getActivity(), "Uploading Image..").show();
                        progressBar.setVisibility(View.VISIBLE);
                        storageReference.child("posts/images/" + Calendar.getInstance().getTimeInMillis() + ".jpg").putFile(imageURI).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                @SuppressWarnings("VisibleForTests") Uri downloadURI = taskSnapshot.getDownloadUrl();
                                imageURI = downloadURI;
                                progressBar.setVisibility(View.GONE);
                                Toasty.info(getActivity(), "Ready to post").show();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        Toasty.warning(getActivity(), "Invalid Image", 300).show();
                    }
                }

            }


        });

        return v;
    }

    public void startImageIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 0);
    }

    public void saveNewPost(Post post) {
        progressBar.setVisibility(View.VISIBLE);
        if (firebaseUser != null) {
            post.setPostedBy(firebaseUser.getUid());
            post.setTimeStamp(Calendar.getInstance().getTimeInMillis() + "");

        }

        firebaseDatabase.getReference("Post/").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toasty.success(getActivity(), "Success.").show();
            }
        });
    }

    public void commendToPost() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postNewButton:
                savePost = new Post();
                savePost.setPostBody(postBody.getText().toString());
                postBody.setText("");
                postBody.setHint("Write Something Inspiring here.");
                if (imageURI != null)
                    savePost.setImageURL(imageURI.toString());
                saveNewPost(savePost);
                break;
            case R.id.imageButton2:
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        postsList.clear();
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            postsList.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(Post.class));
        }
        postAdapter = new PostAdapter(postsList);
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
