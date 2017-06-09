package net.net16.xhoemawn.suicideprevention.activity;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.net16.xhoemawn.suicideprevention.Model.Message;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.MessageAdapter;

/**
 * Created by xhoemawn12 on 5/8/17.
 */

public class MessageActivity extends AppCompatActivity implements ValueEventListener {

    private Calendar calendar;
    public static String chat_id = "CHAT_ID";
    public static String CHAT_ID = null;
    private long timestamp = 0;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HashMap<String, Message> messageList;
    private EditText editText;
    private RecyclerView recyclerView;
    private Uri imageURI;
    private ImageButton imageButton;
    private MessageAdapter messageAdapter;
    private Context cntext;
    private StorageReference storageReference;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImageIntent();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MessageActivity.this, permissions[0])) {
                    Snackbar.make(findViewById(R.id.sendText), "Unable to Access Storage..", Snackbar.LENGTH_SHORT).show();
                } else {
                    startImageIntent();
                }
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagefragment);
        cntext = getApplicationContext();
        messageList = new HashMap<>();
        imageURI = null;
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    if (ContextCompat.checkSelfPermission(cntext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(cntext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {*/
                ActivityCompat.requestPermissions(MessageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
/*
                }*/
            }

        });
        editText = (EditText) findViewById(R.id.sendText);

        CHAT_ID = getIntent().getExtras().getString(chat_id);

        recyclerView = (RecyclerView) findViewById(R.id.messageRecycler);

        calendar = Calendar.getInstance();
        timestamp = calendar.getTimeInMillis();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Message/" + CHAT_ID);
        databaseReference.orderByChild("timeStamp").addValueEventListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()

        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMessage();
                    editText.setText("");
                }
                return false;
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        System.out.println("CAHSADP:::" + CHAT_ID);

        // layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHorizontalScrollBarEnabled(true);


    }

    public void startImageIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        LinkedHashMap<String, Message> tempMessageHash = new LinkedHashMap<>();
        if (CHAT_ID != null) {

            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()
                    ) {
                tempMessageHash.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(Message.class));
            }
        }

        messageAdapter = new MessageAdapter(tempMessageHash);
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        ;
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading....");
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setTitle("Suicide Prevention");
                progressDialog.setCancelable(false);
                progressDialog.show();
                storageReference.child("images/" + Calendar.getInstance().getTimeInMillis() + ".jpg").putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.hide();
                        @SuppressWarnings("VisibleForTests") Uri downloadURI = taskSnapshot.getDownloadUrl();
                        Log.d("URI", downloadURI.toString());
                        imageURI = downloadURI;
                        sendMessage();
                        Log.d("URI", downloadURI.toString());
                        Snackbar.make(findViewById(R.id.messageLayout), "Success..", Snackbar.LENGTH_SHORT).show();


                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.imageButton), "Unable to Load Image..", Snackbar.LENGTH_SHORT).show();
            }


            // imageview.setImageURI(selectedImage);


        }
    }

    public void sendMessage() {
        Message message = new Message();
        message.setChatId(CHAT_ID);
        message.setMessageBody(editText.getText().toString());
        message.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        message.setTimeStamp(Calendar.getInstance().getTimeInMillis())
        ;

        if (imageURI != null) {
            message.setImageURI(imageURI.toString());
            Log.d("URI_BEFORE_SAVE", imageURI.toString());
        }
        databaseReference = firebaseDatabase.getReference("Message/" + message.getChatId());
        databaseReference.push().setValue(message);
    }
}
