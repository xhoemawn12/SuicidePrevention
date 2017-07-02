package net.net16.xhoemawn.suicideprevention.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.net16.xhoemawn.suicideprevention.R;
import net.net16.xhoemawn.suicideprevention.adapter.MessageAdapter;
import net.net16.xhoemawn.suicideprevention.base.SuperActivity;
import net.net16.xhoemawn.suicideprevention.callbacks.ImageResult;
import net.net16.xhoemawn.suicideprevention.model.Message;
import net.net16.xhoemawn.suicideprevention.model.Report;
import net.net16.xhoemawn.suicideprevention.tools.ReportType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * Created by xhoemawn12 on 5/8/17.
 */

public class MessageActivity extends SuperActivity implements ValueEventListener {

    private Calendar calendar;
    public static String chat_id = "CHAT_ID";
    public static String CHAT_ID = null;
    private long timestamp = 0;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HashMap<String, Message> messageList;
    private EditText editText;
    private RecyclerView recyclerView;
    private Uri imageURI;
    private ImageButton imageButton;
    private Button reportButton;
    private MessageAdapter messageAdapter;
    private Context cntext;
    private StorageReference storageReference;
    private ImageButton chatButton;
    Uri selectedImage;
    private ProgressBar progressBar;
    private LinkedHashMap<String, Message> tempMessageHash;
    private LinearLayoutManager layoutManager;
    private String reportAgainst;
    private String reportedBy;
    private Integer reportType = ReportType.SPAM;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImageIntent();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MessageActivity.this, permissions[0])) {

                    Toasty.warning(MessageActivity.this, "Unable To Access Storage", 300).show();
                } else {
                    startImageIntent();
                }
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagefragment);
        cntext = getApplicationContext();
        tempMessageHash = new LinkedHashMap<>();
        messageList = new HashMap<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        imageURI = null;
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        chatButton = (ImageButton) findViewById(R.id.sendButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MessageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

        });
        editText = (EditText) findViewById(R.id.sendText);
        CHAT_ID = getIntent().getExtras().getString(chat_id);
        recyclerView = (RecyclerView) findViewById(R.id.messageRecycler);
        calendar = Calendar.getInstance();
        timestamp = calendar.getTimeInMillis();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Message/" + CHAT_ID);
        databaseReference.addValueEventListener(this);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                if (!message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
          /*          NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(MessageActivity.this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("New Message").setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                    Intent resultIntent = new Intent(MessageActivity.this, MessageActivity.class);
                    resultIntent.putExtra("CHAT_ID", dataSnapshot.getKey());
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MessageActivity.this);
                    stackBuilder.addParentStack(MessageActivity.class);

                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());*/
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              if (!editText.getText().toString().equals("") || selectedImage != null) {
                                                  sendMessage();
                                                  editText.setText("");
                                              }
                                          }
                                      }
        );
        layoutManager = new LinearLayoutManager(getApplicationContext());
        // layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHorizontalScrollBarEnabled(true);

        messageAdapter = new MessageAdapter(tempMessageHash);
        recyclerView.setAdapter(messageAdapter);
        setImageResult(new ImageResult() {
            @Override
            public void resultStatus(Boolean boo,Intent data,Integer type) {
                if(boo && type == 0){
                    selectedImage = data.getData();
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        storageReference = firebaseStorage.getReference();
                        final ProgressDialog progressDialog = new ProgressDialog(MessageActivity.this);
                        Toasty.info(MessageActivity.this,"Uploading Image..").show();
                            progressBar.setVisibility(View.VISIBLE);
                        storageReference.child("users/images/" + Calendar.getInstance().getTimeInMillis() + ".jpg").putFile(selectedImage).addOnSuccessListener(MessageActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                @SuppressWarnings("VisibleForTests") Uri downloadURI = taskSnapshot.getDownloadUrl();

                                imageURI = downloadURI;
                                sendMessage();
                                imageURI = null;
                                progressBar.setVisibility(View.GONE);
                                Toasty.info(MessageActivity.this, "Uploaded").show();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        Toasty.warning(MessageActivity.this, "Invalid Image", 300).show();
                    }
                }

            }


        });
        reportButton = (Button) findViewById(R.id.reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageActivity.this);
                alertDialogBuilder.setView(R.layout.createreportdialog);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                final EditText body = (EditText) alertDialog.findViewById(R.id.body);
                final RadioGroup radioGroup = (RadioGroup) alertDialog.findViewById(R.id.radioGroup);
                final CheckBox checkBox = (CheckBox)  alertDialog.findViewById(R.id.checkBox);
                final Button reportBtn = (Button) alertDialog.findViewById(R.id.submit);
                alertDialog.show();
                reportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.pornographic: reportType = ReportType.PORNOGRAPHIC_CONTENT;
                        break;
                    case R.id.impolite: reportType = ReportType.IMPOLITE;
                        break;
                    case R.id.others: reportType = ReportType.OTHERS;
                        break;
                }
                if(checkBox.isSelected()) {alertDialog.dismiss();

                    Bitmap bitmap = MessageActivity.this.getWindow().getDecorView().getRootView().getDrawingCache();
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    String path = MediaStore.Images.Media.insertImage(MessageActivity.this.getContentResolver(), bitmap, "tempFile", null);

                    FirebaseStorage.getInstance().getReference("reports/images/" + Calendar.getInstance().getTimeInMillis() + ".jpg").putFile(Uri.parse(path)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            createReport(body.getText().toString(), reportType, taskSnapshot.getDownloadUrl().toString());
                        }
                    });
                }

                    }
                });
            }
        });
    }

    public void startImageIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 0);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        tempMessageHash.clear();
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()
                ) {
            tempMessageHash.put(dataSnapshot1.getKey(), dataSnapshot1.getValue(Message.class));
        }
        messageAdapter.notifyDataSetChanged();
        if (tempMessageHash.size() != 0) {
            recyclerView.smoothScrollToPosition(tempMessageHash.size() - 1);
        }
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void sendMessage() {
        System.out.println(ServerValue.TIMESTAMP.toString());
        progressBar.setVisibility(View.VISIBLE);
        Message message = new Message();
        message.setChatId(CHAT_ID);
        message.setMessageBody(editText.getText().toString());
        message.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        message.setTimeStamp(Calendar.getInstance().getTimeInMillis())
        ;
        message.setImageURI("");

        if (imageURI != null) {
            message.setImageURI(imageURI.toString());
            Log.d("URI_BEFORE_SAVE", imageURI.toString());
        }
        databaseReference = firebaseDatabase.getReference("Message/" + message.getChatId());
        databaseReference.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
            }
        });
        firebaseDatabase.getReference("Chat/"+message.getChatId()).child("/read/").setValue(false);

    }
    public void createReport(final String reportDesc , final Integer reportType, final String imageURL ){

        FirebaseDatabase.getInstance().getReference("Chat/"+CHAT_ID).child("/users/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(!Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getUid(), dataSnapshot1.getKey()))
                    reportAgainst = dataSnapshot1.getKey();
                }
                if(reportAgainst!=null){
                    Report report = new Report();
                    report.setReportedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    report.setReportedTo(reportAgainst);
                    report.setReportDescription(reportDesc);
                    report.setReportType(reportType);
                    report.setReviewed(false);
                    report.setReportImage(imageURL);
                    FirebaseDatabase.getInstance().getReference("Report/").push().setValue(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(MessageActivity.this,"Successfully Reported.").show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
