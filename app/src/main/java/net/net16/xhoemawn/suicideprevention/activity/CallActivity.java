package net.net16.xhoemawn.suicideprevention.activity;

import android.Manifest;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.model.User;

public class CallActivity extends SuperActivity {

    private Call call;
    private TextView callState;
    private Button button;
    private Button cancelButton;
    private TextView caller;
    private ImageView callerImage;
    private String recipientId;
    private String type;
    Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("receiver");
        type = "";
        caller = (TextView) findViewById(R.id.name);
        callState = (TextView) findViewById(R.id.textCaller);
        type = intent.getStringExtra("type");
        button = (Button) findViewById(R.id.callButton);
        cancelButton = (Button) findViewById(R.id.dontAnswer);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveCall.hangup();
            }
        });
        callerImage = (ImageView) findViewById(R.id.callerImage);
        if (type !=null && type.equals("receiving")) {
            ringtone = RingtoneManager.getRingtone(getApplicationContext(),RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            ringtone.play();
            FirebaseDatabase.getInstance().getReference("User/"+receiveCall.getRemoteUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if(user!=null){
                        caller.setText(user.getName());
                        if(user.getImageURL()!=null){
                            Glide.with(callerImage).load(user.getImageURL()).into(callerImage);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            button.setText("Answer Call");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button.getText().toString().equals("Hangup")) {
                        if (receiveCall != null) {
                            receiveCall.hangup();
                            //callState.setText(receiveCall.getDetails().getDuration()+"");
                        }
                    }
                    else{
                    ringtone.stop();
                    ActivityCompat.requestPermissions(CallActivity.this, new String[]{
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE
                    }, 2);
                     button.setText("Hangup");
                    }
                }
            });
        } else {
            cancelButton.setVisibility(View.GONE);
            callerImage.setVisibility(View.GONE);
            callState.setVisibility(View.GONE);
            caller.setVisibility(View.GONE);
            ActivityCompat.requestPermissions(CallActivity.this, new String[]{
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE
            }, 3);
            button.setText("Hangup");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button.getText().toString().equals("Hangup")) {
                        if(call!=null){
                            call.hangup();
                        }
                        button.setText("Call Now");
                    }
                    else{
                        ActivityCompat.requestPermissions(CallActivity.this, new String[]{
                                Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE
                        }, 3);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 && grantResults.length > 0) {
            call = sinchClient.getCallClient().callUser(recipientId);
            call.addCallListener(new SinchCallListener());
            button.setText("Hangup");
        }
        else if(requestCode==2 && grantResults.length>0){
            if(receiveCall!=null)
                receiveCall.answer();
        }
    }

}
