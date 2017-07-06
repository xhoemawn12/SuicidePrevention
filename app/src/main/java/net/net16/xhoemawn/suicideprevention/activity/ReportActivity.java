package net.net16.xhoemawn.suicideprevention.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.model.Report;
import net.net16.xhoemawn.suicideprevention.model.User;

import org.w3c.dom.Text;

/**
 * Created by xhoemawn12 on 7/1/17.
 */

public class ReportActivity extends SuperActivity implements View.OnClickListener{
    private TextView reportedBy;
    private TextView reportedTo;
    private TextView reportType;
    private TextView reportBody;
    private Report report;
    private String reportId;
    private ImageView image;
    private Button blockUser;
    private Button reportSpam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        report = null;
        reportId = null;
        Bundle bundle = getIntent().getExtras();
        report = (Report) bundle.get("REPORT");
        reportId = (String) bundle.get("REPORTID");
        reportBody = (TextView) findViewById(R.id.reportedDescription);
        reportedTo = (TextView) findViewById(R.id.reportedTo);
        image = (ImageView) findViewById(R.id.content);
        reportedBy = (TextView) findViewById(R.id.reportedBy);
        reportBody.setText(report.getReportDescription());
        reportedBy.setText(report.getReportedBy());
        reportedTo.setText(report.getReportedTo());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("User/");
        databaseReference.child(report.getReportedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportedBy.setText(dataSnapshot.getValue(User.class).getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child(report.getReportedTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportedTo.setText(dataSnapshot.getValue(User.class).getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reportedBy.setOnClickListener(this);
        reportedTo.setOnClickListener(this);
        Glide.with(ReportActivity.this).load(report.getReportImage()).into(image);
        blockUser = (Button) findViewById(R.id.blockButton);
        reportSpam = (Button) findViewById(R.id.reportSpamButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reportedBy:
                Intent intent = new Intent(this,UserProfileActivity.class);
                intent.putExtra("USERID",report.getReportedBy());
                startActivity(intent);
                break;
            case R.id.reportedTo:

                Intent intent1 = new Intent(this,UserProfileActivity.class);
                intent1.putExtra("USERID",report.getReportedTo());
                startActivity(intent1);
                break;
            case R.id.blockButton:
                FirebaseDatabase.getInstance().getReference("Report/"+reportId).child("/reviewed/").setValue(true);
                FirebaseDatabase.getInstance().getReference("User/"+report.getReportedTo()).child("/disabled/").setValue(System.currentTimeMillis());
        }
    }
}
