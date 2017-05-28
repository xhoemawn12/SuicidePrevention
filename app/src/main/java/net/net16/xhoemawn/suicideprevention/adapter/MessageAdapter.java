package net.net16.xhoemawn.suicideprevention.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import net.net16.xhoemawn.suicideprevention.Model.Message;
import net.net16.xhoemawn.suicideprevention.Model.User;
import net.net16.xhoemawn.suicideprevention.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;

/**
 * Created by xhoemawn12 on 5/10/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    LinkedHashMap<String,Message> messageHashMap;
    List<Message> messages;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String senderName;
    public MessageAdapter(){

    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelayout,parent,false);
        messages = new ArrayList<>(messageHashMap.values());
        for(Message message: messages){
            System.out.println("TEST!:"+message.getMessageBody());
        }
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        System.out.println(messages.get(position).getMessageBody());
    holder.message.setText(messages.get(position).getMessageBody());

            databaseReference.child(messages.get(position).getSenderId()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            senderName = dataSnapshot.getValue(User.class).getName();
                            holder.sender.setText(senderName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
            holder.linearlayout.setPadding(0,40,0,0);
            holder.imageView.setVisibility(View.GONE);
            if (messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.linearlayout.setGravity(Gravity.END);
            }
            if (messages.get(position).getImageURI() != null) {
                holder.imageView.setVisibility(View.GONE);
                holder.imageView.setImageURI(messages.get(position).getImageURI());
            }
        }



    @Override
    public int getItemCount() {
        return messageHashMap.size();
    }

    public MessageAdapter(LinkedHashMap<String,Message> messageHashMap){

        this.messageHashMap = messageHashMap;


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");

    }

    public static class MessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView sender;
        ImageView imageView;
        LinearLayout linearlayout;
        public MessageHolder(View v){
            super(v);
            linearlayout = (LinearLayout) v.findViewById(R.id.linearLayoutChat);
            message = (TextView)v.findViewById(R.id.message);
            sender = (TextView) v.findViewById(R.id.senderId);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
