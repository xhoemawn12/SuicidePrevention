package net.net16.xhoemawn.suicideprevention;

import net.net16.xhoemawn.suicideprevention.activity.SignupActivity;
import net.net16.xhoemawn.suicideprevention.model.User;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * Created by xhoemawn12 on 7/8/17.
 */

@PrepareForTest(SignupActivity.class)
public class SignUpActivityTest {
    @Mock
    SignupActivity signupActivity;

    @Test
    public void signup() {
        signupActivity = Mockito.mock(SignupActivity.class);
        User user = new User();
        user.setEmail("mockito@test.com");
        user.setPassword("mockitotest123");
        signupActivity.signUpUser(user);
    }
}
