package just.application.androidtransportinvestigator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.smartdevicelink.transport.MultiplexTransportConfig;

import static just.application.androidtransportinvestigator.Defines.TransportType.LBT;
import static just.application.androidtransportinvestigator.Defines.TransportType.MBT;
import static just.application.androidtransportinvestigator.Defines.TransportType.TCP;
import static just.application.androidtransportinvestigator.Defines.btSecurityLvlKey;
import static just.application.androidtransportinvestigator.Defines.btTypeKey;
import static just.application.androidtransportinvestigator.Defines.tcpIpKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int TCP_POPUP_ACTIVITY_REQUEST_CODE = 0;
    private static final int BT_POPUP_ACTIVITY_REQUEST_CODE = 1;

    private Defines.TransportType transportType = Defines.TransportType.TCP;
    private int bluetoothSecurityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF;
    private Defines.TransportType btType = MBT;
    private String userIp = "127.0.0.1"; //172.31.239.143


    RadioGroup transportRadioGroup;
    RadioButton btnBt, btnUsb, btnTcp;
    Button btnAdjustTransport, btnAcceptTransport;
    TextView logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitWidgets();

        registerListener();
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
                userIp = data.getStringExtra(tcpIpKey);
                break;

            case BT_POPUP_ACTIVITY_REQUEST_CODE:
                btType = (Defines.TransportType)data.getSerializableExtra(btTypeKey);
                bluetoothSecurityLevel = data.getIntExtra(Defines.btSecurityLvlKey, MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF);
                break;

            default:
                Logger.Debug(logger, TAG,"onActivityResult: Unrecognized request code");

        }
    }

    private void InitWidgets() {
        btnBt = (RadioButton) findViewById(R.id.rdBtnBt);
        btnUsb = (RadioButton) findViewById(R.id.rdBtnUsb);
        btnTcp = (RadioButton) findViewById(R.id.rdBtnTcp);

        transportRadioGroup = (RadioGroup) findViewById(R.id.transportRadioGroup);

        btnAdjustTransport = (Button) findViewById(R.id.btnAdjustTransport);
        btnAcceptTransport = (Button) findViewById(R.id.btnAcceptTransport);

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

        btnAcceptTransport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /**
                 * Setting needed fields and creating proxy
                 */
                if (transportType.equals(MBT)) {
                    Logger.Debug(logger, TAG, "SdlReceiver.queryForConnectedService()");
                    SdlReceiver.queryForConnectedService(v.getContext());
                } else {
                    if (transportType.equals(TCP) || transportType.equals(LBT)) {
                        Intent proxyIntent = new Intent(v.getContext(), SdlService.class);
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
        });

        btnAdjustTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (transportRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.rdBtnBt:
                        Intent btSettingsIntent = new Intent(view.getContext(), BtPopupActivity.class);
                        btSettingsIntent.putExtra(btTypeKey, btType);
                        btSettingsIntent.putExtra(btSecurityLvlKey, bluetoothSecurityLevel);

                        startActivityForResult(btSettingsIntent, BT_POPUP_ACTIVITY_REQUEST_CODE);
                        break;

                    case R.id.rdBtnTcp:
                        Intent tcpSettingsIntent = new Intent(view.getContext(), TcpPopupActivity.class);
                        tcpSettingsIntent.putExtra(tcpIpKey, userIp);

                        startActivityForResult(tcpSettingsIntent, TCP_POPUP_ACTIVITY_REQUEST_CODE);
                        break;
                }
            }
        });
    }
}
