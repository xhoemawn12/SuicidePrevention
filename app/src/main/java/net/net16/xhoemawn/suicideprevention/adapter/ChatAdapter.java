package net.net16.xhoemawn.suicideprevention.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.Model.Chat;
import net.net16.xhoemawn.suicideprevention.Model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.fragment.MessageActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by xhoemawn on 4/8/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private HashMap<String,Chat> chatHashMap;
    private String[] chatIds;
    private List<Chat> chats;
    private Drawable drawable ;
    private User foreignUser;
    public ChatAdapter(){

    }
    public ChatAdapter(HashMap<String,Chat> chat){
        this.chatHashMap = chat;
    }
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_layout, parent, false);
        chats = new ArrayList<>(chatHashMap.values());
        chatIds = chatHashMap.keySet().toArray(new String[chatHashMap.keySet().size()]);
        //drawable = v.getResources().getDrawable(R.drawable.com_facebook_button_login_silver_background,null);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        holder.chatName.setText(chats.get(position).getNameOfChat());
        HashMap<String, Boolean> usersInvolved = chats.get(position).getUsers();
        Set<String> usersId = usersInvolved.keySet();
        String foreignUserId = null;

        for(String userId: usersId){
            if(!Objects.equals(userId, FirebaseAuth.getInstance().getCurrentUser().getUid())){
                System.out.println(userId);
                foreignUserId = userId;
            }
        }
        if(foreignUserId != null) {
            System.out.println(foreignUserId);
            FirebaseDatabase.getInstance().getReference("User/").child(foreignUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    foreignUser = (User) dataSnapshot.getValue(User.class);
                    if (foreignUser != null)
                        holder.userName.setText(foreignUser.getName());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //holder.imageView.setImageURI(foreignUser.getImageURI());
        }
    }



    @Override
    public int getItemCount() {
        System.out.println(chatHashMap.size());
        return chatHashMap.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView chatName;
        TextView userName;
        ImageView imageView;
        ViewHolder(View v){
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageURL);
            chatName = (TextView) v.findViewById(R.id.chatName);
            userName = (TextView) v.findViewById(R.id.userName);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context,MessageActivity.class);
            intent.putExtra("CHAT_ID",chatIds[getAdapterPosition()]);
            context.startActivity(intent);


        }
    }

}
