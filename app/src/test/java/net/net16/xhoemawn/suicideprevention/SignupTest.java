package net.net16.xhoemawn.suicideprevention;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.net16.xhoemawn.suicideprevention.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by xhoemawn12 on 7/8/17.
 */


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({FirebaseAuth.class})
public class SignupTest {
    @Mock
    private User signUpUser;
    @Mock
    private FirebaseAuth firebaseAuth;

    @Before
    public void setReady() {
        FirebaseAuth mockedFirebaseDatabase = Mockito.mock(FirebaseAuth.class);
        PowerMockito.mockStatic(FirebaseAuth.class);
        Mockito.when(FirebaseAuth.getInstance()).thenReturn(firebaseAuth);
    }

    @Mock
    FirebaseUser firebaseUser;

    @Test
    public void signUser() {
        final Task
                task = Mockito.mock(Task.class);
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        System.out.println(firebaseUser.getUid());
        when(firebaseAuth.signInWithEmailAndPassword("khadkasuman20@gmail.com", "Suman235")).thenReturn(task);
        System.out.println(task.isSuccessful());
    }


}
