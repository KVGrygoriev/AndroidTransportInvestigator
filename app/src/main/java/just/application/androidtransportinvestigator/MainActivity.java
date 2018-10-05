package just.application.androidtransportinvestigator;

import just.application.androidtransportinvestigator.Logger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Switch swtchBtSupport, swtchUsbSupport, swtchTcpSupport;
    TextView logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swtchBtSupport = (Switch) findViewById(R.id.swtchBtSupport);
        swtchUsbSupport = (Switch) findViewById(R.id.swtchUsbSupport);
        swtchTcpSupport = (Switch) findViewById(R.id.swtchTcpSupport);

        logger = (TextView) findViewById(R.id.loggerDisplay);

        registerListener();
    }

    private void registerListener() {
        swtchBtSupport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.Debug(logger, TAG, buttonView.getText() + " got state " + (isChecked ? "CHECKED" : "UNCHECKED"));
            }
        });

        swtchUsbSupport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.Debug(logger, TAG, buttonView.getText() + " got state " + (isChecked ? "CHECKED" : "UNCHECKED"));
            }
        });

        swtchTcpSupport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.Debug(logger, TAG, buttonView.getText() + " got state " + (isChecked ? "CHECKED" : "UNCHECKED"));
            }
        });
    }
}
