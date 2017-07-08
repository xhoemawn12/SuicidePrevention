package net.net16.xhoemawn.suicideprevention.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.net16.xhoemawn.suicideprevention.model.User;
import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.activity.UserProfileActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListHolder> {
    private LinkedHashMap<String, User> userHashMap;
    private ArrayList<User> userArrayList;
    private ArrayList<String> idList;

    public UserListAdapter(LinkedHashMap<String, User> userHashMap) {
        this.userHashMap = userHashMap;

    }

    @Override
    public UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.userdetail, parent, false);

        return new UserListHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserListHolder holder, int position) {
        holder.userName.setText(userArrayList.get(position).getName());
        if (userArrayList.get(position).getImageURL() != null)
            Glide.with(holder.imageView).load(userArrayList.get(position).getImageURL()).into(holder.imageView);
        holder.userDescription.setText(userArrayList.get(position).getDescription());
        holder.userStatus.setText(userArrayList.get(position).isAvailable()?"online":"offline"+ "");

// holder.userType.setText(userArrayList.get(position).getUserType());

    }

    @Override
    public int getItemCount() {
        userArrayList = new ArrayList<>(userHashMap.values());
        idList
                = new ArrayList<>(userHashMap.keySet());
        return userHashMap.size();
    }

    class UserListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView userName;
        private ImageView imageView;
        private TextView userDescription;
        private TextView userStatus;

        UserListHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.newMessage);
            userStatus = (TextView) itemView.findViewById(R.id.status);
            imageView = (ImageView) itemView.findViewById(R.id.userImage);
            /*List<Integer> colorList = new ArrayList<>();
            colorList.add(Color.RED);
            colorList.add(Color.YELLOW);
            colorList.add(Color.BLACK
            );
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 10, 0, 0);
            itemView.setLayoutParams(lp);
            itemView.setBackgroundColor(colorList.get(new Random().nextInt(2)));*/
            userDescription = (TextView) itemView.findViewById(R.id.userDescription);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
            intent.putExtra("USERID", idList.get(getAdapterPosition()));
            view.getContext().startActivity(intent);
        }
    }

}
