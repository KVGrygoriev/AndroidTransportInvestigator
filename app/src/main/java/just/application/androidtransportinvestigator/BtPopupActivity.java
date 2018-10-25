package just.application.androidtransportinvestigator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smartdevicelink.transport.MultiplexTransportConfig;

import static just.application.androidtransportinvestigator.Defines.TransportType.LBT;
import static just.application.androidtransportinvestigator.Defines.TransportType.MBT;
import static just.application.androidtransportinvestigator.Defines.BT_SECURITY_LVL_KEY;
import static just.application.androidtransportinvestigator.Defines.BT_TYPE_KEY;

public class BtPopupActivity extends AppCompatActivity {

    private static final String TAG = "BtPopupActivity";

    RadioGroup rdBtnGrpBtType, rdBtnGrpBtSecurityLevel;
    RadioButton rdBtnMBT, rdBtnLBT, rdBtnSecLvlHigh, rdBtnSecLvlMedium, rdBtnSecLvlLow, rdBtnSecLvlOff;
    Button btnAccept, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_popup);
        setTitle("Bluetooth settings");

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

        switch ((Defines.TransportType)getIntent().getSerializableExtra(BT_TYPE_KEY)) {
            case MBT:
                rdBtnMBT.setChecked(true);
                EnableGroupButtonBtSecurityLevel(false);
                break;

            case LBT:
                rdBtnLBT.setChecked(true);
                break;

            default:
                Log.e(TAG, "Bluetooth type doesn't set!");
                break;
        }

        switch (getIntent().getIntExtra(BT_SECURITY_LVL_KEY, MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF)) {
            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF:
                rdBtnSecLvlOff.setChecked(true);
                break;

            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_HIGH:
                rdBtnSecLvlHigh.setChecked(true);
                break;

            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED:
                rdBtnSecLvlMedium.setChecked(true);
                break;

            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_LOW:
                rdBtnSecLvlLow.setChecked(true);
                break;

            default:
                Log.e(TAG, "Bluetooth security level doesn't set!");
                break;
        }
    }

    private void InitWidgets() {
        rdBtnGrpBtType = (RadioGroup) findViewById(R.id.rdBtnGrpBtType);
        rdBtnGrpBtSecurityLevel = (RadioGroup) findViewById(R.id.rdBtnGrpBtSecurityLevel);

        rdBtnMBT = (RadioButton) findViewById(R.id.rdBtnMBT);
        rdBtnLBT = (RadioButton) findViewById(R.id.rdBtnLBT);
        rdBtnSecLvlHigh = (RadioButton) findViewById(R.id.rdBtnSecLvlHigh);
        rdBtnSecLvlMedium = (RadioButton) findViewById(R.id.rdBtnSecLvlMedium);
        rdBtnSecLvlLow = (RadioButton) findViewById(R.id.rdBtnSecLvlLow);
        rdBtnSecLvlOff = (RadioButton) findViewById(R.id.rdBtnSecLvlOff);

        btnAccept = (Button) findViewById(R.id.btnAcceptBtSettings);
        btnCancel = (Button) findViewById(R.id.btnCancelBtSettings);
    }

    private void registerListener() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();

                switch (rdBtnGrpBtType.getCheckedRadioButtonId()) {
                    case R.id.rdBtnMBT:
                        intent.putExtra(BT_TYPE_KEY, MBT);
                        break;

                    case R.id.rdBtnLBT:
                        intent.putExtra(BT_TYPE_KEY, LBT);
                        break;

                    default:
                        Log.e(TAG,"Unrecognized BT type!");
                        break;
                }

                switch (rdBtnGrpBtSecurityLevel.getCheckedRadioButtonId()) {
                    case R.id.rdBtnSecLvlHigh:
                        intent.putExtra(BT_SECURITY_LVL_KEY, MultiplexTransportConfig.FLAG_MULTI_SECURITY_HIGH);
                        break;

                    case R.id.rdBtnSecLvlMedium:
                        intent.putExtra(BT_SECURITY_LVL_KEY, MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED);
                        break;

                    case R.id.rdBtnSecLvlLow:
                        intent.putExtra(BT_SECURITY_LVL_KEY, MultiplexTransportConfig.FLAG_MULTI_SECURITY_LOW);
                        break;

                    case R.id.rdBtnSecLvlOff:
                        intent.putExtra(BT_SECURITY_LVL_KEY, MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF);
                        break;

                    default:
                        Log.e(TAG,"Unrecognized BT security level!");
                        break;
                }

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        rdBtnGrpBtType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                boolean enableSecurityLevelSettings = true;

                switch (checkedId) {
                    case R.id.rdBtnMBT:
                        enableSecurityLevelSettings = false;
                        break;

                    default:
                        break;
                }

                EnableGroupButtonBtSecurityLevel(enableSecurityLevelSettings);
            }
        });
    }

    void EnableGroupButtonBtSecurityLevel(boolean enable) {
        for (int i = 0; i < rdBtnGrpBtSecurityLevel.getChildCount(); ++i) {
            ((RadioButton)rdBtnGrpBtSecurityLevel.getChildAt(i)).setEnabled(enable);
        }
    }
}
