package just.application.androidtransportinvestigator;

import just.application.androidtransportinvestigator.Defines;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Defines.TransportType transportType = Defines.TransportType.TCP;

    RadioButton btnBt, btnUsb, btnTcp;
    Button btnAdjustTransport, btnAcceptTransport;
    TextView logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBt = (RadioButton) findViewById(R.id.rdBtnBt);
        btnUsb = (RadioButton) findViewById(R.id.rdBtnUsb);
        btnTcp = (RadioButton) findViewById(R.id.rdBtnTcp);

        btnAdjustTransport = (Button) findViewById(R.id.btnAdjustTransport);
        btnAcceptTransport = (Button) findViewById(R.id.btnAcceptTransport);

        logger = (TextView) findViewById(R.id.loggerDisplay);
        logger.setMovementMethod(new ScrollingMovementMethod());

        registerListener();
    }

    private void registerListener() {
        btnBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transportType = Defines.TransportType.BT;
                Logger.Debug(logger, TAG, ((Button)v).getText() + " transport selected");
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
                Intent proxyIntent = new Intent(v.getContext(), SdlService.class);
                proxyIntent.putExtra("TransportType", /*transportType*/"TCP");

                if (Defines.TransportType.BT == transportType) {
                    //TODO pass BluetoothType
                    //TODO pass BluetoothSecurityLevel
                }

                startService(proxyIntent);

            }
        });

        btnAdjustTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO handler
            }
        });
    }
}
