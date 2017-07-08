package net.net16.xhoemawn.suicideprevention.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.calling.Call;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;

public class CallActivity extends SuperActivity {

    private Call call;
    private TextView callState;
    private Button button;
    private String callerId;
    private String recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("receiver");
        button = (Button) findViewById(R.id.callButton);
        callState = (TextView) findViewById(R.id.callState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    ActivityCompat.requestPermissions(CallActivity.this, new String[]{
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE
                    }, 3);

                } else {
                    call.hangup();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 && grantResults.length > 0) {
            call = sinchClient.getCallClient().callUser(recipientId);
            call.addCallListener(new SinchCallListener());
            button.setText("Hang Up");
        }
    }

}
