package net.net16.xhoemawn.suicideprevention.fragment;

import android.app.Activity;
import android.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private HashMap<String,Message> messageList;
    private EditText editText;
    private RecyclerView recyclerView;
    private Image image;
    private ImageButton imageButton;
    private MessageAdapter messageAdapter;
    private Context cntext;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagefragment);
        cntext = getApplicationContext();
        messageList = new HashMap<>();
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Tbus");
                ActivityCompat.requestPermissions(MessageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},0);
                if(ContextCompat.checkSelfPermission(cntext, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 1);
                }
                else{

                    Snackbar.make(MessageActivity.super.findViewById(R.id.imageButton),"Unable to access storage.",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        editText = (EditText) findViewById(R.id.sendText);
        CHAT_ID = getIntent().getExtras().getString(chat_id);
        recyclerView = (RecyclerView) findViewById(R.id.messageRecycler);
        calendar = Calendar.getInstance();
        timestamp = calendar.getTimeInMillis();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Message/"+CHAT_ID);
        databaseReference.orderByChild("timeStamp").addValueEventListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    sendMessage();
                    editText.setText("");
                }
                return false;
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        System.out.println("CAHSADP:::"+CHAT_ID);

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

                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()
                            ) {
                        tempMessageHash.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Message.class));


                    }
                }

            messageAdapter = new MessageAdapter(tempMessageHash);
            recyclerView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();;
        }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                   // imageview.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                   // imageview.setImageURI(selectedImage);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImage.getPath());
                    imageButton.setImageBitmap(bitmap);

                }
                break;
        }
    }
    public void sendMessage(){
        Message message = new Message();
        message.setChatId(CHAT_ID);
        message.setMessageBody(editText.getText().toString());
        message.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        message.setTimeStamp(Calendar.getInstance().getTimeInMillis())
        ;
        databaseReference = firebaseDatabase.getReference("Message/"+message.getChatId());
        databaseReference.push().setValue(message);
    }
}
