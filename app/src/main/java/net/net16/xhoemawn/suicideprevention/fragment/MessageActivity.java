package net.net16.xhoemawn.suicideprevention.fragment;

import android.app.Fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private MessageAdapter messageAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagefragment);
        messageList = new HashMap<>();
        editText = (EditText) findViewById(R.id.sendText);
        CHAT_ID = getIntent().getExtras().getString(chat_id);
        recyclerView = (RecyclerView) findViewById(R.id.messageRecycler);
        calendar = Calendar.getInstance();
        timestamp = calendar.getTimeInMillis();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Message/");
        databaseReference.orderByChild("chatId").equalTo(CHAT_ID).addValueEventListener(this);
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
        messageAdapter = new MessageAdapter(messageList);


    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (CHAT_ID != null) {
            for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()
                 ) {
                messageList.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Message.class));
                System.out.println(dataSnapshot1.getValue(Message.class).getChatId());;

            }
            recyclerView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();;
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void sendMessage(){
        Message message = new Message();
        message.setChatId(CHAT_ID);
        message.setMessageBody(editText.getText().toString());
        message.setTimeStamp(Calendar.getInstance().getTimeInMillis())
        ;
        databaseReference = firebaseDatabase.getReference("Message/");
        databaseReference.push().setValue(message);
    }
}
