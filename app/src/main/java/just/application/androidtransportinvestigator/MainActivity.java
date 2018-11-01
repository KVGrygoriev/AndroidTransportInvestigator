package just.application.androidtransportinvestigator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.smartdevicelink.transport.MultiplexTransportConfig;

import static just.application.androidtransportinvestigator.BroadcastLogger.LOGGER_LVL;
import static just.application.androidtransportinvestigator.Defines.TransportType.LBT;
import static just.application.androidtransportinvestigator.Defines.TransportType.MBT;
import static just.application.androidtransportinvestigator.Defines.TransportType.TCP;
import static just.application.androidtransportinvestigator.Defines.BT_SECURITY_LVL_KEY;
import static just.application.androidtransportinvestigator.Defines.BT_TYPE_KEY;
import static just.application.androidtransportinvestigator.Defines.TCP_IP_KEY;
import static just.application.androidtransportinvestigator.BroadcastLogger.LOGGER_MSG;
import static just.application.androidtransportinvestigator.BroadcastLogger.LOGGER_TAG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int TCP_POPUP_ACTIVITY_REQUEST_CODE = 0;
    private static final int BT_POPUP_ACTIVITY_REQUEST_CODE = 1;

    private Defines.TransportType transportType = Defines.TransportType.TCP;
    private int bluetoothSecurityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF;
    private Defines.TransportType btType = MBT;
    private String userIp = "127.0.0.1"; //172.31.239.143

    BroadcastReceiver loggerBroadcastReceiver;

    RadioGroup transportRadioGroup;
    RadioButton btnBt, btnUsb, btnTcp;
    Button btnAdjustTransport, btnAcceptResetTransport;
    TextView logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitWidgets();

        registerListener();

        InitLoggerBrodcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(loggerBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            Logger.Warning(logger, TAG,"onActivityResult: resultCode is " + resultCode);
            return;
        }

        switch (requestCode) {
            case TCP_POPUP_ACTIVITY_REQUEST_CODE:
                userIp = data.getStringExtra(TCP_IP_KEY);
                break;

            case BT_POPUP_ACTIVITY_REQUEST_CODE:
                btType = (Defines.TransportType)data.getSerializableExtra(BT_TYPE_KEY);
                bluetoothSecurityLevel = data.getIntExtra(Defines.BT_SECURITY_LVL_KEY, MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF);
                break;

            default:
                Logger.Debug(logger, TAG,"onActivityResult: Unrecognized request code");

        }
    }

    /*
    * loggerBroadcastReceiver uses for log messages delivery from services/activity.
    * And further output them to the log widget.
    */
    private void InitLoggerBrodcastReceiver() {
        loggerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch ((Defines.LogLevel)intent.getSerializableExtra(LOGGER_LVL)) {
                    case INFO:
                        Logger.Info(logger, intent.getStringExtra(LOGGER_TAG),  "[i] " + intent.getStringExtra(LOGGER_MSG));
                        break;

                    case DEBUG:
                        Logger.Debug(logger, intent.getStringExtra(LOGGER_TAG), "[d] " + intent.getStringExtra(LOGGER_MSG));
                        break;

                    case ERROR:
                        Logger.Error(logger, intent.getStringExtra(LOGGER_TAG), "[e] " + intent.getStringExtra(LOGGER_MSG));
                        break;

                    case VERBOSE:
                        Logger.Verbose(logger, intent.getStringExtra(LOGGER_TAG), "[v] " + intent.getStringExtra(LOGGER_MSG));
                        break;

                    case WARNING:
                        Logger.Warning(logger, intent.getStringExtra(LOGGER_TAG), "[w] " + intent.getStringExtra(LOGGER_MSG));
                        break;

                    default:
                        Log.w(TAG, "Undetermined log level type");
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(Defines.BroadcastLoggerId.MAIN_ACTIVITY.toString());
        registerReceiver(loggerBroadcastReceiver, intentFilter);
    }

    private void InitWidgets() {
        btnBt = (RadioButton) findViewById(R.id.rdBtnBt);
        btnUsb = (RadioButton) findViewById(R.id.rdBtnUsb);
        btnTcp = (RadioButton) findViewById(R.id.rdBtnTcp);

        transportRadioGroup = (RadioGroup) findViewById(R.id.transportRadioGroup);

        btnAdjustTransport = (Button) findViewById(R.id.btnAdjustTransport);
        btnAcceptResetTransport = (Button) findViewById(R.id.btnAcceptResetTransport);

        logger = (TextView) findViewById(R.id.loggerDisplay);
        logger.setMovementMethod(new ScrollingMovementMethod());
    }

    private String BtSecurityLevelToString(int securityLevel) {
        switch (securityLevel) {
            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_LOW:
                return "FLAG_MULTI_SECURITY_LOW";

            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED:
                return "FLAG_MULTI_SECURITY_MED";

            case MultiplexTransportConfig.FLAG_MULTI_SECURITY_HIGH:
                return "FLAG_MULTI_SECURITY_HIGH";

            default:
                break;
        }

        return "FLAG_MULTI_SECURITY_OFF";
    }

    private void registerListener() {
        btnBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Logger.Debug(logger, TAG, ((Button)v).getText() + "(" + btType.name()
                        + ") transport selected; "
                        + BtSecurityLevelToString(bluetoothSecurityLevel)
                        + " security level");

                transportType = btType;
            }
        });

        btnUsb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transportType = Defines.TransportType.USB;
                Logger.Debug(logger, TAG, ((Button)v).getText() + " transport selected");
            }
        });

        btnTcp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transportType = Defines.TransportType.TCP;
                Logger.Debug(logger, TAG, ((Button)v).getText() + " transport selected");
            }
        });

        btnAcceptResetTransport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (btnAcceptResetTransport.getText().toString().equals(getResources().getString(R.string.btnAcceptTransport))) { //TODO make another way
                    btnAcceptResetTransport.setText(R.string.btnResetTransport);


                    EnableWidgets(false);

                    StartSDLTransportService(v.getContext());
                } else {
                    btnAcceptResetTransport.setText(R.string.btnAcceptTransport);

                    EnableWidgets(true);

                    StopSDLTransportService(v.getContext());
                }
            }
        });

        btnAdjustTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (transportRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.rdBtnBt:
                        Intent btSettingsIntent = new Intent(view.getContext(), BtPopupActivity.class);
                        btSettingsIntent.putExtra(BT_TYPE_KEY, btType);
                        btSettingsIntent.putExtra(BT_SECURITY_LVL_KEY, bluetoothSecurityLevel);

                        startActivityForResult(btSettingsIntent, BT_POPUP_ACTIVITY_REQUEST_CODE);
                        break;

                    case R.id.rdBtnTcp:
                        Intent tcpSettingsIntent = new Intent(view.getContext(), TcpPopupActivity.class);
                        tcpSettingsIntent.putExtra(TCP_IP_KEY, userIp);

                        startActivityForResult(tcpSettingsIntent, TCP_POPUP_ACTIVITY_REQUEST_CODE);
                        break;
                }
            }
        });

        transportRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rdBtnUsb:
                        btnAdjustTransport.setEnabled(false);
                        break;

                    default:
                        btnAdjustTransport.setEnabled(true);
                        break;
                }
            }
        });
    }

    private void StopSDLTransportService(Context context) {
        Intent proxyIntent = new Intent(context, SdlService.class);

        stopService(proxyIntent);
    }

    private void StartSDLTransportService(Context context) {

        /**
         * Setting needed fields and creating proxy
         */
        if (transportType.equals(MBT)) {
            Logger.Debug(logger, TAG, "SdlReceiver.queryForConnectedService()");
            SdlReceiver.queryForConnectedService(context);
        } else {
            if (transportType.equals(TCP) || transportType.equals(LBT)) {
                Intent proxyIntent = new Intent(context, SdlService.class);
                proxyIntent.putExtra("TransportType", transportType);

                switch (transportType) {
                    case MBT:
                        proxyIntent.putExtra("SecurityLevel", bluetoothSecurityLevel);
                        break;

                    case TCP:
                        proxyIntent.putExtra("UserIp", userIp);

                    default:
                        break;
                }

                startService(proxyIntent);
            }
        }
    }

    private void EnableWidgets(boolean value) {
        btnAdjustTransport.setEnabled(value);

        for (int i = 0 ; i < transportRadioGroup.getChildCount(); ++i) {
            ((RadioButton)transportRadioGroup.getChildAt(i)).setEnabled(value);
        }
    }
}
