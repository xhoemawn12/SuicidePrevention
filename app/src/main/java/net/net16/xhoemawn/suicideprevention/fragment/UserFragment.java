package net.net16.xhoemawn.suicideprevention.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.net16.xhoemawn.suicideprevention.callbacks.OnProfilePicChanged;
import net.net16.xhoemawn.suicideprevention.model.Chat;
import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.activity.MessageActivity;
import net.net16.xhoemawn.suicideprevention.callbacks.ReadyToCreateChat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class UserFragment extends Fragment
        implements ValueEventListener, View.OnClickListener {
    private Button chatButton;
    private Button commendButton;
    private OnProfilePicChanged onProfilePicChanged;
    private User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference chatDataReference;
    private TextView userName;
    private TextView userType;
    private boolean chatCreationStatus = false;
    private TextView aboutUser;
    private ImageView profilePic;
    private static final String userId = "USERID";
    private static String FOREIGNUSERID = null;
    private static String DOMESTICUSERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String chatId = null;
    private ProgressBar progressBar;
    private Uri imageURI = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference();
                progressBar.setVisibility(View.VISIBLE);
                storageReference.child("user/images/" + Calendar.getInstance().getTimeInMillis() + ".jpg").putFile(selectedImage).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        @SuppressWarnings("VisibleForTests") Uri downloadURI = taskSnapshot.getDownloadUrl();

                        imageURI = downloadURI;
                        if (imageURI != null)
                            user.setImageURL(imageURI.toString());
                        updateProfile(user);
                        imageURI = null;
                        Toasty.info(getActivity(), "Uploaded").show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Toasty.warning(getActivity(), "Invalid Image", 300).show();
            }
        }
    }

    public void updateProfile(User user) {

        FirebaseDatabase.getInstance().getReference("User/" + FOREIGNUSERID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toasty.success(getActivity(), "Success").show();
                    ;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImageIntent();
            } else {
                if (shouldShowRequestPermissionRationale(permissions[0])) {

                    Toasty.warning(getActivity(), "Unable To Access Storage", 300).show();
                } else {
                    startImageIntent();
                }
            }
        }

    }

    public void startImageIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commend) {
            /*FirebaseAuth.getInstance().signOut();
            ;
            startActivity(new Intent(getActivity(), LoginActivity.class));
            FirebaseDatabase.getInstance().purgeOutstandingWrites();
            getActivity().finish();*/
            final DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("User/"+FOREIGNUSERID).child("/commends/");
            dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer commends = dataSnapshot.getValue(Integer.class);

                    if(commends!=null){
                        commends++;
                    }
                    else{
                        commends = 1;
                    }
                    dbr.setValue(commends);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (v.getId() == R.id.chat) {
            createNewChat(new ReadyToCreateChat() {
                @Override
                public void onReady(boolean stat) {
                    if (stat) {
                        Intent intent = new Intent(UserFragment.this.getActivity(), MessageActivity.class);
                        intent.putExtra("CHAT_ID", chatId);
                        if (chatId != null) {
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
            });

        }
    }

    public static UserFragment newInstance(String id) {
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(userId, id);
        userFragment.setArguments(bundle);
        return userFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userprofilebeta, container, false);
        FOREIGNUSERID = getArguments().getString(userId);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");
        chatDataReference = firebaseDatabase.getReference("Chat/");

        userName = (TextView) view.findViewById(R.id.newMessage);
        chatButton = (Button) view.findViewById(R.id.chat);
        commendButton = (Button) view.findViewById(R.id.commend);
        userType = (TextView) view.findViewById(R.id.userType);
        aboutUser = (TextView) view.findViewById(R.id.userDescription);
        chatButton.setOnClickListener(this);
        commendButton.setOnClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        profilePic = (ImageView) view.findViewById(R.id.profilePic);
        onProfilePicChanged = new OnProfilePicChanged() {
            @Override
            public void isChanged(Boolean boo) {
                if (boo) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (user != null)
                        if (user.getImageURL() != null) {
                            Glide.with(getActivity()).load(user.getImageURL()).into(profilePic);
                            progressBar.setVisibility(View.GONE);

                        }
                }

            }
        };
        databaseReference.child(FOREIGNUSERID).addValueEventListener(this);
        if (Objects.equals(FOREIGNUSERID, DOMESTICUSERID)) {
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    Toasty.info(getActivity(), "Changing Profile Pic").show();
                    ;
                }

            });
        }

        return view;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

        Log.d("Database Error. ", "Error Code of:" + databaseError.getCode() + "");

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);

        if (user != null) {
            userName.setText(user.getName());
            onProfilePicChanged.isChanged(true);
            if (user.getUserType() != 1)
                userType.setText(user.getUserType() == 0 ? "Helper" : "Victim");
            else
                userType.setText("Moderator");
            if(user.getDescription()!=null)
                aboutUser.setText(user.getDescription());
        }

    }

    public String createNewChat(final ReadyToCreateChat readyToCreateChat) {
        if (FOREIGNUSERID != null) {
            final LinkedHashMap<String, Chat> chatHashMap = new LinkedHashMap<>();

            HashMap<String, Boolean> usersList = new HashMap<>();
            usersList.put(FOREIGNUSERID, true);
            usersList.put(DOMESTICUSERID, true);
            final Chat chat = new Chat("0", usersList);
            chat.setTimeStamp(Calendar.getInstance().getTimeInMillis());
            chat.setLastMessage("");
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    readyToCreateChat.onReady(false);
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        chatHashMap.put(d.getKey(), d.getValue(Chat.class));
                    }
                    final ArrayList<Chat> chatArrayList1 = new ArrayList<>(chatHashMap.values());

                    String[] chatIds = chatHashMap.keySet().toArray(new String[chatHashMap.keySet().size()]);
                    chatCreationStatus = true;
                    System.out.println(chatHashMap.size() + "     " + chatArrayList1.size() + "Length of chatIds List" + chatIds.length);
                    if (chatArrayList1.size() != 0) {
                        for (int i = 0; i < chatArrayList1.size(); i++) {
                            System.out.println(chatArrayList1.get(i).getUsers().containsKey(DOMESTICUSERID));
                            System.out.println(chatArrayList1.get(i).getUsers().containsKey(FOREIGNUSERID));
                            if (chatArrayList1.get(i).getUsers().containsKey(DOMESTICUSERID) && chatArrayList1.get(i).getUsers().containsKey(FOREIGNUSERID)) {
                                chatId = chatIds[i];
                                chatCreationStatus = false;
                                break;
                            }
                        }
                    }
                    if (chatCreationStatus) {
                        chatId = chatDataReference.push().getKey();
                        chatDataReference.child(chatId).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                }
                            }
                        });
                    }
                    readyToCreateChat.onReady(true);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            chatDataReference.orderByChild("users/" + DOMESTICUSERID).equalTo(true).addListenerForSingleValueEvent(valueEventListener);
        }
        return chatId;
    }


}
