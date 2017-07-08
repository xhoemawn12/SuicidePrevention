package net.net16.xhoemawn.suicideprevention.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.model.Chat;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Created by xhoemawn on 4/12/2017.
 */

public class ChatFragment extends Fragment {

    private LinkedHashMap<String, Chat> chatHashMap;
    private LinkedHashMap<String, Boolean> userHashMap;
    private static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private static String USERID = "USERID";

    public static ChatFragment newInstance(String userId) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERID, userId);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }
    private LinkedHashMap<String,Chat> sortByTimeStamp(LinkedHashMap<String,Chat> chatHash){
        LinkedHashMap<String,Chat> tempChat = new LinkedHashMap<>();
        ArrayList<Long> timestamps = new ArrayList<>();
        for(Chat chat: chatHash.values()){
            timestamps.add(chat.getTimeStamp());
        }
        Collections.sort(timestamps);
        Collections.reverse(timestamps);
        ArrayList<Chat> chats = new ArrayList<>(chatHashMap.values());
        ArrayList<String> chatIds = new ArrayList<String>(chatHashMap.keySet());
        for(Long timestamp: timestamps){
            for(int i = 0; i<chatHash.size();i++){
                if(timestamp.equals(chats.get(i).getTimeStamp())){
                    if(!tempChat.containsKey(chatIds.get(i))){
                        tempChat.put(chatIds.get(i),chats.get(i));
                    }
                }
            }
        }
        return tempChat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chatfragment, container, false);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view1);
        userHashMap = new LinkedHashMap<>();
        chatHashMap = new LinkedHashMap<>();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHorizontalScrollBarEnabled(true);
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Chat/");
        String childKey = "users/" + firebaseUser.getUid();
        System.out.println(childKey);

        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress);

        final TextView textView1 = (TextView) rootView.findViewById(R.id.messageAlert);
        textView1.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.orderByChild(childKey).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               chatHashMap.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    chatHashMap.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(Chat.class));
                }
                if(chatHashMap.size()==0){
                    textView1.setVisibility(View.VISIBLE);

                }
                else{
                    textView1.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
                chatHashMap = sortByTimeStamp(chatHashMap);
                ChatAdapter chatAdapter = new ChatAdapter(chatHashMap);
                recyclerView.setAdapter(chatAdapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        System.out.println(databaseReference.toString());

/*


        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    userHashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),true);
                    userHashMap.put(USERID,true);
                    Chat tempChat = new Chat(edit.getText().toString(),userHashMap);
                    databaseReference.push().setValue(tempChat);
                    chatAdapter.notifyDataSetChanged();
                    edit.setText("");
                    recyclerView.requestFocus();
                }
                return false;
            }
        });
*/

        return rootView;

    }

}
