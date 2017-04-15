package net.net16.xhoemawn.suicideprevention.database;

import android.net.ConnectivityManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.net16.xhoemawn.suicideprevention.Model.Chat;

import java.util.ArrayList;

/**
 * Created by glenn on 4/14/17.
 */
public class FetchDatabase {

    private static  FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;

    public static  FetchDatabase getInstance(){
        FetchDatabase fetchDatabase = new FetchDatabase();
        firebaseDatabase  = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        return new FetchDatabase();
    }

    public ArrayList<Chat> getChatData(){
        return new ArrayList<>();
    }


    public ArrayList getPostDetails(){
        return new ArrayList();
    }

    public DatabaseReference getChatNode(){
        return databaseReference.child("Child");
    }

}
