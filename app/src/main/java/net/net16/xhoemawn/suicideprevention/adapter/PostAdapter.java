package net.net16.xhoemawn.suicideprevention.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.model.Post;
import net.net16.xhoemawn.suicideprevention.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private LinkedHashMap<String, Post> postHashMap;
    private ArrayList<Post> posts;
    private DateFormat dateFormat;
    private ArrayList<String> keys;

    public PostAdapter(LinkedHashMap<String, Post> postHashMap) {
        this.postHashMap = postHashMap;

    }


    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        dateFormat = new SimpleDateFormat("YY:MM HH:mm", Locale.US);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, int position) {
        Post tempPost = posts.get(position);
        holder.postBody.setText(tempPost.getPostBody());
        FirebaseDatabase.getInstance().getReference("User/" + tempPost.getPostedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.postedBy.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (!tempPost.getPostedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.deletebutton.setVisibility(View.GONE);
            holder.textView.setVisibility(View.GONE);
        }
        if (tempPost.getImageURL() == null) {
            holder.imageBody.setVisibility(View.GONE);
        } else {
            Glide.with(holder.imageBody).load(tempPost.getImageURL()).into(holder.imageBody);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(tempPost.getTimeStamp()));
        holder.timeStamp.setText(dateFormat.format(calendar.getTime()) + "");
        if (tempPost.getCommends() == null) {
            tempPost.setCommends(new HashMap<String, Boolean>());
        }
        if (tempPost.getCommends().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            tempPost.getCommends().put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
            //FirebaseDatabase.getInstance().getReference("Post/"+keys.get(position)).child("/commends/").setValue(tempPost.getCommends());
            holder.commends.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        holder.commendCount.setText(tempPost.getCommends().size() + " Commends");

    }

    @Override
    public int getItemCount() {
        posts = new ArrayList<>(postHashMap.values());
        Collections.reverse(posts);
        keys = new ArrayList<>(postHashMap.keySet());
        Collections.reverse(keys);
        return posts.size();
    }

    class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageBody;
        private TextView postedBy;
        private TextView postBody;
        private TextView timeStamp;
        private TextView commendCount;
        private ImageButton commends;
        private ImageButton deletebutton;
        private TextView textView;

        PostHolder(final View itemView) {
            super(itemView);
            imageBody = (ImageView) itemView.findViewById(R.id.postImage);
            postedBy = (TextView) itemView.findViewById(R.id.newMessage);
            postBody = (TextView) itemView.findViewById(R.id.postBody);
            timeStamp = (TextView) itemView.findViewById(R.id.timestamp);
            commendCount = (TextView) itemView.findViewById(R.id.commendText);
            deletebutton = (ImageButton) itemView.findViewById(R.id.delete);
            commends = (ImageButton) itemView.findViewById(R.id.commendButton);
            textView = (TextView) itemView.findViewById(R.id.deleteText);
            commends.setOnClickListener(this);
            deletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Post/" + keys.get(getAdapterPosition())).removeValue();
                    Toasty.success(itemView.getContext(), "Removed.").show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            Post post = posts.get(getAdapterPosition());
            if (post.getCommends() == null) {
                post.setCommends(new HashMap<String, Boolean>());
            }
            if (!post.getCommends().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                post.getCommends().put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                FirebaseDatabase.getInstance().getReference("Post/" + keys.get(getAdapterPosition())).child("/commends/").setValue(post.getCommends());
                commends.setImageResource(R.drawable.ic_favorite_black_24dp);
            }

        }
    }
}
