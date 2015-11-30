package cs294.riviera.com.riviera_student;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by tonywu on 11/28/15.
 */
public class RivieraApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "B0GS111ytFTrli3CciVw2gFuI5LENEkXxZ1jiwBD", "RpJoBrqgJfyGtCvxju9nowsdT2tcCpFY4q3TUR0i");
        ParseUser.enableRevocableSessionInBackground();
    }

}
