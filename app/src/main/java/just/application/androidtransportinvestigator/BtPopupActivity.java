package just.application.androidtransportinvestigator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;

import static just.application.androidtransportinvestigator.Defines.btSecurityLvlKey;
import static just.application.androidtransportinvestigator.Defines.btTypeKey;

public class BtPopupActivity extends AppCompatActivity {

    private static final String TAG = "BtPopupActivity";

    RadioButton rdBtnMBT, rdBtnLBT, rdBtnSecLvlHigh, rdBtnSecLvlMedium, rdBtnSecLvlLow, rdBtnSecLvlOff;
    Button btnAccept, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_popup);

        InitWindowSize();

        InitWidgets();
        ApplyWidgetSettings();

        registerListener();
    }

    private void InitWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width *.9), (int)(height *.37));
    }

    private void ApplyWidgetSettings() {
        //TODO
        switch (getIntent().getStringExtra(btTypeKey)) {
            default:
                Log.e(TAG, "Bluetooth type doesn't set!");
                break;
        }

        switch (getIntent().getStringExtra(btSecurityLvlKey)) {
            default:
                Log.e(TAG, "Bluetooth security level doesn't set!");
                break;
        }
    }

    private void InitWidgets() {
        rdBtnMBT = (RadioButton) findViewById(R.id.rdBtnMBT);
        rdBtnLBT = (RadioButton) findViewById(R.id.rdBtnLBT);
        rdBtnSecLvlHigh = (RadioButton) findViewById(R.id.rdBtnSecLvlHigh);
        rdBtnSecLvlMedium = (RadioButton) findViewById(R.id.rdBtnSecLvlMedium);
        rdBtnSecLvlLow = (RadioButton) findViewById(R.id.rdBtnSecLvlLow);
        rdBtnSecLvlOff = (RadioButton) findViewById(R.id.rdBtnSecLvlOff);

        btnAccept = (Button) findViewById(R.id.btnAcceptBtSettings);
        btnCancel = (Button) findViewById(R.id.btnAcceptBtSettings);
    }


    //TODO change window headline
    //TODO redesign TCP popup

    private void registerListener() {
        //TODO init listeners
    }
}
