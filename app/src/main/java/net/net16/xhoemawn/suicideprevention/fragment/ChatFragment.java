package net.net16.xhoemawn.suicideprevention.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.Model.Chat;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.ChatAdapter;

import java.util.HashMap;

/**
 * Created by xhoemawn on 4/12/2017.
 */

public class ChatFragment extends Fragment{

   private HashMap<String,Chat> chatHashMap;
    private HashMap<String,Boolean> userHashMap;
    private static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private static String USERID = "USERID";

    public static ChatFragment  newInstance(String userId){
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERID,userId);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  rootView = inflater.inflate(R.layout.chatfragment, container, false);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view1);
        userHashMap = new HashMap<>();
        chatHashMap = new HashMap<>();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final ChatAdapter chatAdapter = new ChatAdapter(chatHashMap);

        // layoutManager.setReverseLayout(true);
       // layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHorizontalScrollBarEnabled(true);
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Chat/");
        String childKey = "users/"+firebaseUser.getUid();
        System.out.println(childKey);
        databaseReference.orderByChild(childKey).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 // chatHashMap = new HashMap<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            System.out.println(dataSnapshot1.getKey());
                            chatHashMap.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(Chat.class));

                        }
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
