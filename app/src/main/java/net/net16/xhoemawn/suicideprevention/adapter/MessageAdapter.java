package net.net16.xhoemawn.suicideprevention.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.model.Message;
import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by xhoemawn12 on 5/10/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private LinkedHashMap<String, Message> messageHashMap;
    private List<Message> messages;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String senderName;

    public MessageAdapter() {

    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelayout, parent, false);

        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        holder.message.setVisibility(View.VISIBLE);
        cal.setTimeInMillis(messages.get(position).getTimeStamp());
        holder.message.setText(messages.get(position).getMessageBody());
        databaseReference.child(messages.get(position).getSenderId()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            senderName = dataSnapshot.getValue(User.class).getName();
                            holder.sender.setText(senderName);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        holder.linearlayout.setPadding(0, 10, 0, 0);

        holder.imageView.setVisibility(View.GONE);
        if (messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.linearlayout.setGravity(Gravity.END);

        }
        else{
            holder.linearlayout.setGravity(Gravity.START);
        }
        if (!messages.get(position).getImageURI().equals("")) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            Uri uri = Uri.parse(messages.get(position).getImageURI());

            Glide.with(holder.imageView).load(messages.get(position).getImageURI()).into(holder.imageView);

        }
    }


    @Override
    public int getItemCount() {
        messages = new ArrayList<>(messageHashMap.values());
        return messageHashMap.size();
    }

    public MessageAdapter(LinkedHashMap<String, Message> messageHashMap) {

        this.messageHashMap = messageHashMap;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User/");

    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView sender;
        ImageView imageView;
        LinearLayout linearlayout;

        public MessageHolder(View v) {
            super(v);
            linearlayout = (LinearLayout) v.findViewById(R.id.linearLayoutChat);
            message = (TextView) v.findViewById(R.id.message);
            sender = (TextView) v.findViewById(R.id.senderId);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
