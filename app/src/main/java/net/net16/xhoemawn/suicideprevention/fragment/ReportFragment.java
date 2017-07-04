package net.net16.xhoemawn.suicideprevention.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.ReportAdapter;
import net.net16.xhoemawn.suicideprevention.model.Report;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by xhoemawn12 on 6/30/17.
 */

public class ReportFragment extends Fragment {

    private RecyclerView recyclerView ;
    private ReportAdapter reportAdapter;
    private LinkedHashMap<String, Report> reportHashMap;

    public static ReportFragment newInstance(){
        ReportFragment reportFragment = new ReportFragment();
        return reportFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_fragment,container,false);
        reportHashMap = new LinkedHashMap<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewReport);
        reportAdapter = new ReportAdapter(reportHashMap);
        FirebaseDatabase.getInstance().getReference("Report/").orderByChild("reviewed").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportHashMap = new LinkedHashMap<String, Report>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    reportHashMap.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Report.class));
                }
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }
}
