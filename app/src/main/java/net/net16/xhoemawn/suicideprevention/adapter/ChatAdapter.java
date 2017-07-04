package net.net16.xhoemawn.suicideprevention.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.model.Chat;
import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.activity.MessageActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by xhoemawn on 4/8/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private LinkedHashMap<String,Chat> chatHashMap;
    private List<Chat> chats;
    private List<String> chatIds;
    private Drawable drawable ;
    private User foreignUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public ChatAdapter(){

    }
    public ChatAdapter(LinkedHashMap<String, Chat> chat){
        this.chatHashMap = chat;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Chat");
    }
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_layout, parent, false);

        //chatIds = chatHashMap.keySet().toArray(new String[chatHashMap.keySet().size()]);

        //drawable = v.getResources().getDrawable(R.drawable.com_facebook_button_login_silver_background,null);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.chatName.setText(chats.get(position).getNameOfChat());
        HashMap<String, Boolean> usersInvolved = chats.get(position).getUsers();
        Set<String> usersId = usersInvolved.keySet();
        String foreignUserId = null;
        for(String userId: usersId){
            if(!Objects.equals(userId, FirebaseAuth.getInstance().getCurrentUser().getUid())){
                foreignUserId = userId;
                if(userId.equals(chats.get(position).getLastMessage())) {
                    holder.newMessage.setText("New Message");
                    holder.constraintLayout.setBackgroundResource(R.color.colorPrimaryLight);
                }
            }

        }

        if(foreignUserId != null) {
           firebaseDatabase.getReference("User/").child(foreignUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    foreignUser = (User) dataSnapshot.getValue(User.class);
                    if (foreignUser != null) {
                        holder.chatName.setText(foreignUser.getName());
                        if(foreignUser.getImageURL()!=null)
                            Glide.with(holder.imageView).load(foreignUser.getImageURL()).into(holder.imageView);

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }



    @Override
    public int getItemCount() {
        chats = new ArrayList<>(chatHashMap.values());
        Collections.reverse(chats);
        chatIds = new ArrayList<String>(chatHashMap.keySet());
        Collections.reverse(chatIds);

        return chatHashMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView chatName;
        private TextView newMessage;
        private ImageView imageView;
        private ConstraintLayout constraintLayout;
        ViewHolder(View v){
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageURL);
            chatName = (TextView) v.findViewById(R.id.chatName);
            newMessage = (TextView) v.findViewById(R.id.newMessage);
            constraintLayout = (ConstraintLayout) v.findViewById(R.id.constraintLayoutChat);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context,MessageActivity.class);
            intent.putExtra("CHAT_ID",chatIds.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }

}
