package just.application.androidtransportinvestigator;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LockScreenActivity extends AppCompatActivity {

    public static void registerActivityLifecycle(Application application) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
