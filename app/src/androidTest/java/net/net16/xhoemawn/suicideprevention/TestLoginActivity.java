package net.net16.xhoemawn.suicideprevention;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TestLoginActivity {

    @Test
    public void testLogin() {
        String enteredEmail = "hello@hello.com";
        FirebaseAuth.getInstance().signInWithEmailAndPassword("hello1@asd.com", "hello123");

        Assert.assertEquals(enteredEmail, FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }


}