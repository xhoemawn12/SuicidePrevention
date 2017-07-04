package net.net16.xhoemawn.suicideprevention.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.callbacks.ImageResult;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SuperActivity extends
        AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private static boolean shouldShowNotification = true;
    private NotificationManager notificationManager;
    public ImageResult imageResult;

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
        notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.getValue(Boolean.class)) {
                    Toasty.error(getApplicationContext(), "Cannot Connect to Server", Toast.LENGTH_LONG).show();

                    if (shouldShowNotification) {
                        notificationManager = buildNotification("Cannot Connect To Server","Check your Internet Connection.",new Intent(),0,notificationManager,true);
                        shouldShowNotification = false;
                    }
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

    public NotificationManager buildNotification(String title, String msg, Intent intent, Integer idOfNotification, NotificationManager  notificationMgr,Boolean shouldAutoCancel) {
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
        notificationMgr.notify(idOfNotification,mBuilder.build());
            mBuilder.setAutoCancel(shouldAutoCancel);
        return notificationMgr;
    }

}