package net.net16.xhoemawn.suicideprevention.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.Model.Chat;
import net.net16.xhoemawn.suicideprevention.Model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.activity.LoginActivity;
import net.net16.xhoemawn.suicideprevention.activity.MessageActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class UserFragment extends Fragment
        implements ValueEventListener, View.OnClickListener {
    private Button chatButton;
    private Button commendButton;
    private User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference chatDataReference;
    private TextView userName;
    private EditText userType;
    private boolean chatCreationStatus = false;
    private EditText aboutUser;
    private static final String userId = "USERID";
    private static String FOREIGNUSERID = null;
    private static String DOMESTICUSERID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String chatId = null;
    @Override
    public void onClick(View v){
        if(v.getId()==R.id.commend){
            FirebaseAuth.getInstance().signOut();;
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        else if(v.getId()==R.id.chat){
            createNewChat(new ReadyToCreateChat() {
                @Override
                public void onReady(boolean stat) {
                    if(stat){
                        Intent intent = new Intent(UserFragment.this.getActivity(), MessageActivity.class);
                        intent.putExtra("CHAT_ID",chatId);
                        if(chatId!=null) {
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
        View view = inflater.inflate(R.layout.user_profile, container, false);
        FOREIGNUSERID = getArguments().getString(userId);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");
        chatDataReference = firebaseDatabase.getReference("Chat/");
        databaseReference.child(FOREIGNUSERID).addValueEventListener(this);
        userName = (TextView) view.findViewById(R.id.chatName);
        chatButton = (Button) view.findViewById(R.id.chat);
        commendButton = (Button) view.findViewById(R.id.commend);
        userType = (EditText) view.findViewById(R.id.userType);
        aboutUser = (EditText) view.findViewById(R.id.userAbout);
        chatButton.setOnClickListener(this);
        commendButton.setOnClickListener(this);
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
            if (user.isAvailable()) {
                System.out.println("helloworld");
                aboutUser.setText(String.format("%s", user.isAvailable()));
            }
        }

    }

    public String createNewChat(final ReadyToCreateChat readyToCreateChat) {
        if(FOREIGNUSERID!=null){
            final LinkedHashMap<String,Chat> chatHashMap = new LinkedHashMap<>();

            HashMap<String, Boolean> usersList = new HashMap<>();
            usersList.put(FOREIGNUSERID, true);
            usersList.put(DOMESTICUSERID, true);
            final Chat chat = new Chat("0",usersList);
            chat.setTimeStamp(Calendar.getInstance().getTimeInMillis());

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    readyToCreateChat.onReady(false);
                    for(DataSnapshot d: dataSnapshot.getChildren()){
                        chatHashMap.put(d.getKey(),d.getValue(Chat.class));
                    }
                    final ArrayList<Chat> chatArrayList1 = new ArrayList<>(chatHashMap.values());

                    String[]  chatIds = chatHashMap.keySet().toArray(new String[chatHashMap.keySet().size()]);
                    chatCreationStatus = true;
                    System.out.println(chatHashMap.size()+"     "+chatArrayList1.size()+"Length of chatIds List"+chatIds.length);
                    if(chatArrayList1.size()!=0) {
                        for (int i = 0; i < chatArrayList1.size(); i++) {
                            System.out.println(chatArrayList1.get(i).getUsers().containsKey(DOMESTICUSERID));
                            System.out.println(chatArrayList1.get(i).getUsers().containsKey(FOREIGNUSERID) );
                            if(chatArrayList1.get(i).getUsers().containsKey(DOMESTICUSERID) && chatArrayList1.get(i).getUsers().containsKey(FOREIGNUSERID) ){
                                chatId = chatIds[i];
                                chatCreationStatus = false;
                                break;
                            }
                        }
                    }
                    if(chatCreationStatus) {
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
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Chat/");
            databaseReference.orderByChild("users/"+DOMESTICUSERID).equalTo(true).addListenerForSingleValueEvent(valueEventListener);
        }
        return chatId;
    }

    public interface ReadyToCreateChat{
        public void onReady(boolean stat);
    }
}
