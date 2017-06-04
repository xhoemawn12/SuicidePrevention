package net.net16.xhoemawn.suicideprevention.fragment;

import android.app.Activity;
import android.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
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

import net.net16.xhoemawn.suicideprevention.Manifest;
import net.net16.xhoemawn.suicideprevention.Model.Message;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.ChatAdapter;
import net.net16.xhoemawn.suicideprevention.adapter.MessageAdapter;

import org.w3c.dom.Text;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                boolean bool = ActivityCompat.shouldShowRequestPermissionRationale(MessageActivity.this, permissions[0]);
                if (!bool) {
                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                            , android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

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
                if (ContextCompat.checkSelfPermission(cntext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(cntext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 1);
                }


            }

        });
        editText = (EditText)

                findViewById(R.id.sendText);

        CHAT_ID =

                getIntent().

                        getExtras().

                        getString(chat_id);

        recyclerView = (RecyclerView)

                findViewById(R.id.messageRecycler);

        calendar = Calendar.getInstance();
        timestamp = calendar.getTimeInMillis();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Message/" + CHAT_ID);
        databaseReference.orderByChild("timeStamp").

                addValueEventListener(this);
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
                final StorageReference storageReference = firebaseStorage.getReference();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                storageReference.child("images/" + Calendar.getInstance().getTimeInMillis() + ".jpg").putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.hide();

                        Snackbar.make(findViewById(R.id.messageLayout), "Success..", Snackbar.LENGTH_SHORT).show();
                    }
                });
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURI = uri;
                            System.out.println(uri.toString());
                        }
                    });
                    sendMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.sendButton), "Unable to Load Image..", Snackbar.LENGTH_SHORT).show();
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
        message.setImageURI(imageURI);
        databaseReference = firebaseDatabase.getReference("Message/" + message.getChatId());
        databaseReference.push().setValue(message);
    }
}
