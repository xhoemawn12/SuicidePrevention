package net.net16.xhoemawn.suicideprevention;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;

import java.net.URL;

/**
 * Created by glenn on 4/14/17.
 */

public class ChatData extends AsyncTask<DatabaseReference,Long,Long> {
    public ProgressDialog progressDialog;

    public ChatData(Context ctx) {
        progressDialog = new ProgressDialog(ctx);
    }

    @Override
    protected Long doInBackground(DatabaseReference... dbReference) {

        return null;
    }

    @Override
    protected void onPreExecute() {

        progressDialog.show();
        super.onPreExecute();
    }
}