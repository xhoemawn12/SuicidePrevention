package net.net16.xhoemawn.suicideprevention.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.activity.WelcomeActivity;
import net.net16.xhoemawn.suicideprevention.callbacks.ImageResult;
import net.net16.xhoemawn.suicideprevention.model.Chat;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class SuperActivity extends
        AppCompatActivity {
    private static boolean shouldShowNotification = true;
    private static boolean firstRun = true;
    private static boolean messageActive = false;

    public static boolean isMessageActive() {
        return messageActive;
    }

    public static void setMessageActive(boolean messageActive) {
        SuperActivity.messageActive = messageActive;
    }

    private static int count = 0;
    public ImageResult imageResult;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private NotificationManager notificationManager;
    public static SinchClient sinchClient;

    public ImageResult getImageResult() {
        return imageResult;
    }

    public void setImageResult(ImageResult imageResult) {
        this.imageResult = imageResult;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public void setFirebaseDatabase(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
    }

    public FirebaseStorage getFirebaseStorage() {
        return firebaseStorage;
    }

    public void setFirebaseStorage(FirebaseStorage firebaseStorage) {
        this.firebaseStorage = firebaseStorage;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (firstRun)
            try {

                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            } catch (DatabaseException database) {
                FirebaseDatabase.getInstance();
            }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null)
                            if (!dataSnapshot.getValue(Boolean.class)) {
                                if (shouldShowNotification && count > 0) {
                                    Toasty.error(getApplicationContext(), "Cannot Connect to Server", Toast.LENGTH_LONG).show();
                                    notificationManager = buildNotification("Cannot Connect To Server", "Check your Internet Connection.", new Intent(), 0, notificationManager, true);
                                    shouldShowNotification = false;
                                }
                                count++;
                            } else {
                                notificationManager.cancel(0);
                                shouldShowNotification = true;
                            }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }, 20000);
        if (firstRun && FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseDatabase.getReference("User/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("/available/").setValue(true);
            if (!isMessageActive())
                firebaseDatabase.getReference("Chat/").
                        orderByChild("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .equalTo(true).addValueEventListener(new ValueEventListener() {
                                                                 @Override
                                                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                                                     firstRun = false;
                                                                     for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                                         Chat chat = dataSnapshot1.getValue(Chat.class);
                                                                         if (chat.getLastMessage() != null)
                                                                             if (!chat.getLastMessage().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                                                 NotificationCompat.Builder mBuilder =
                                                                                         new NotificationCompat.Builder(SuperActivity.this)
                                                                                                 .setSmallIcon(R.mipmap.ic_launcher)
                                                                                                 .setContentTitle("Suicide Prevention").setContentText("Unresponded Messages.").setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                                                                                 Intent resultIntent = new Intent(SuperActivity.this, WelcomeActivity.class);
                                                                                 resultIntent.putExtra("CHAT_ID", dataSnapshot.getKey());
                                                                                 PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                                                                         resultIntent, PendingIntent.FLAG_ONE_SHOT);
                                                                                 mBuilder.setContentIntent(resultPendingIntent);
                                                                                 NotificationManager mNotificationManager =
                                                                                         (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                                                 mNotificationManager.notify(0, mBuilder.build());
                                                                             }
                                                                     }
                                                                 }

                                                                 @Override
                                                                 public void onCancelled(DatabaseError databaseError) {

                                                                 }
                                                             }
                );
            firebaseDatabase.getReference("User/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("/available/").onDisconnect().setValue(false);

            sinchClient = Sinch.getSinchClientBuilder()
                    .context(this)
                    .userId(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .applicationKey("3793d7b1-6113-4680-b2cd-81b67317434c")
                    .applicationSecret("UL1bOGZ1lESYhYr3ygT9tQ==")
                    .environmentHost("sandbox.sinch.com")
                    .build();

            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.start();
            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        }

    }

    public class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(final CallClient callClient, final Call incomingCall) {
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(SuperActivity.this);
            alBuilder.setMessage("You are receiving a call.");
            alBuilder.setPositiveButton("Answer Call", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callClient.addCallClientListener(new SinchCallClientListener());
                    incomingCall.answer();
                }
            });
            alBuilder.setNegativeButton("Hang up", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    incomingCall.hangup();
                }
            });
            AlertDialog alertDialog = alBuilder.create();
            /*call.addCallListener(new SuperActivity.SinchCallListener());
            button.setText("Hang Up");
        */
        }
    }

    AlertDialog alertDialog;

    public class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {

            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (resultCode == RESULT_OK && requestCode == 0) {
            imageResult.resultStatus(true, returnedIntent, 0);
        } else if (requestCode == 1 && resultCode == RESULT_OK)
            imageResult.resultStatus(true, returnedIntent, 1);
    }

    public NotificationManager buildNotification(String title, String msg,
                                                 Intent intent, Integer idOfNotification,
                                                 NotificationManager notificationMgr,
                                                 Boolean shouldAutoCancel) {
        notificationMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setPriority(Notification.PRIORITY_MAX);

        mBuilder.setContentIntent(contentIntent);
        notificationMgr.notify(idOfNotification, mBuilder.build());
        mBuilder.setAutoCancel(shouldAutoCancel);
        return notificationMgr;
    }

}