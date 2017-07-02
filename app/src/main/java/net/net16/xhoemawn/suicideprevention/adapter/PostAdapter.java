package net.net16.xhoemawn.suicideprevention.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.model.Post;
import net.net16.xhoemawn.suicideprevention.model.User;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{

    private LinkedHashMap<String, Post> postHashMap;
    private ArrayList<Post> posts;
    private DateFormat dateFormat;
    private ArrayList<String> keys;
    public PostAdapter(LinkedHashMap<String, Post> postHashMap){
        this.postHashMap = postHashMap;

    }


    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, int position) {
        Post tempPost = posts.get(position);
        holder.postBody.setText(tempPost.getPostBody());
        FirebaseDatabase.getInstance().getReference("User/"+tempPost.getPostedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.postedBy.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(tempPost.getImageURL()==null){
            holder.imageBody.setVisibility(View.GONE);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(tempPost.getTimeStamp()));
        holder.timeStamp.setText(dateFormat.format(calendar.getTime())+"");
    }

    @Override
    public int getItemCount() {
        posts = new ArrayList<>(postHashMap.values());
        keys = new ArrayList<>(postHashMap.keySet());
        return posts.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        private ImageView imageBody;
        private TextView postedBy;
        private TextView postBody;
        private TextView timeStamp;
        public PostHolder(View itemView) {
            super(itemView);
            imageBody = (ImageView)itemView.findViewById(R.id.postImage);
            postedBy = (TextView)itemView.findViewById(R.id.postedBy);
            postBody = (TextView)itemView.findViewById(R.id.postBody);
            timeStamp = (TextView)itemView.findViewById(R.id.timestamp);

        }
    }
}
