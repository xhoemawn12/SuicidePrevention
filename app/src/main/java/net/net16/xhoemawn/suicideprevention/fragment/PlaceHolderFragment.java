package net.net16.xhoemawn.suicideprevention;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.Model.Chat;

import java.util.ArrayList;

/**
 * Created by xhoemawn on 4/12/2017.
 */
public class PlaceHolderFragment extends android.support.v4.app.Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";


    public PlaceHolderFragment() {
    }

    public static PlaceHolderFragment newInstance() {
        PlaceHolderFragment fragment = new PlaceHolderFragment();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.section_label);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Chat");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String help = "";
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    help += (String) data.getKey();
                }
                textView.setText(help);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;


        //
    }

}

