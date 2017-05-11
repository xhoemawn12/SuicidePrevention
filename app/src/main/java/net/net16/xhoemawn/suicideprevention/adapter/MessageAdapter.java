package net.net16.xhoemawn.suicideprevention.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by xhoemawn12 on 5/10/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    HashMap<String,Message> messageHashMap;
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
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {

    holder.message.setText(messages.get(position).getMessageBody());
        if(messages.get(position).getSenderId()!=null) {
            databaseReference.child(messages.get(position).getSenderId()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            senderName = dataSnapshot.getValue(User.class).getName();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
            holder.sender.setText(senderName);
            if (messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.message.setGravity(Gravity.END);
                holder.sender.setGravity(Gravity.END);
            }
            if (messages.get(position).getImageURI() != null) {
                holder.imageView.setImageURI(messages.get(position).getImageURI());
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageHashMap.size();
    }

    public MessageAdapter(HashMap<String,Message> messageHashMap){

        this.messageHashMap = messageHashMap;


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");

    }

    public static class MessageHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView sender;
        ImageView imageView;
        public MessageHolder(View v){
            super(v);
            message = (TextView)v.findViewById(R.id.message);
            sender = (TextView) v.findViewById(R.id.senderId);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
