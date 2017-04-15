package net.net16.xhoemawn.suicideprevention;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Visibility;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.Model.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by xhoemawn on 4/12/2017.
 */

public class ChatFragment extends Fragment{

    public ProgressBar progressBar ;
    public ArrayList<Chat> arr = new ArrayList<>();
    public ChatFragment(){

    }

    public static ChatFragment  newInstance(){

        return new ChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  rootView = inflater.inflate(R.layout.recycler_view, container, false);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view1);
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar2);
        final EditText edit = (EditText)rootView.findViewById(R.id.editText3);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final ChatAdapter chatAdapter = new ChatAdapter(arr);

        // layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHorizontalScrollBarEnabled(true);
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Chat/");
        final ProgressDialog progressDialog =  new ProgressDialog(ChatFragment.super.getContext());
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        progressBar.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String,Chat> chat = new HashMap<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    chat.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Chat.class));

                }
                progressDialog.hide();
                System.out.println("HELLOW");
                arr = new ArrayList<>(chat.values());
                recyclerView.setAdapter(new ChatAdapter(arr));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.e("ERRRRROR",chatAdapter.getItemCount()+"");

        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    Chat tempChat = new Chat("1","1","1",edit.getText().toString(),"1");
                    databaseReference.push().setValue(tempChat);
                    chatAdapter.notifyDataSetChanged();
                    edit.setText("");
                    recyclerView.requestFocus();
                }
                return false;
            }
        });

        return rootView;

    }

}
