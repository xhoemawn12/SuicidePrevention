package net.net16.xhoemawn.suicideprevention.adapter;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.net16.xhoemawn.suicideprevention.Model.Chat;
import net.net16.xhoemawn.suicideprevention.R;

import java.util.ArrayList;

/**
 * Created by xhoemawn on 4/8/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public ArrayList<Chat> chat ;
    private Drawable drawable ;
    public ChatAdapter(){

    }
    public ChatAdapter(ArrayList<Chat> chat){
        this.chat = chat;
    }
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_layout, parent, false);
        //drawable = v.getResources().getDrawable(R.drawable.com_facebook_button_login_silver_background,null);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat c = chat.get(position);
        holder.message.setBackground(drawable);
        if(c.getUid_receiver().equals("100")){

            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.CYAN);

        }
        else{
            holder.message.setTextColor(Color.BLACK);
            holder.message.setGravity(Gravity.END);
        }
        holder.message.setText(c.getMessage());
        System.out.println("GOT MESSAGE:"+holder.message.getText());

    }



    @Override
    public int getItemCount() {
        return chat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        ImageView imageView;
        ViewHolder(View v){
            super(v);

            imageView = (ImageView) v.findViewById(R.id.imageView);
            message = (TextView) v.findViewById(R.id.textSent);

        }
    }
}
