package just.application.androidtransportinvestigator;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static just.application.androidtransportinvestigator.Defines.MILISEC_IN_SEC;

public class WifiActivity extends AppCompatActivity {

    Button btnTurnOnWifi, btnCloseApp;
    EditText ssidEdit, passEdit;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        setTitle("WIFI settings");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        InitWindowSize();

        InitWidgets();

        registerListener();
    }

    private void InitWindowSize() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width *.9), (int)(height *.7));
    }

    private void InitWidgets() {

        btnTurnOnWifi = (Button) findViewById(R.id.btnTurnOnWifiSettings);
        btnCloseApp = (Button) findViewById(R.id.btnCloseAppWifiSettings);
        ssidEdit = (EditText) findViewById(R.id.ssidEdit);
        passEdit = (EditText) findViewById(R.id.passEdit);
    }

    private void connectToWifi(String ssid, String password) {

        WifiConfiguration configuration = new WifiConfiguration();

        configuration.SSID = String.format("\"%s\"", ssid);
        configuration.preSharedKey = String.format("\"%s\"", password);

        int netId = wifiManager.addNetwork(configuration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private void enableWifi() {

        wifiManager.setWifiEnabled(true);

        // Some kind of sleep function until WiFi be enabled
        while (WifiManager.WIFI_STATE_ENABLED != wifiManager.getWifiState()) {
            (new Handler()).postDelayed(null, MILISEC_IN_SEC);
        };
    }

    private void registerListener() {

        btnTurnOnWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                enableWifi();

                connectToWifi(ssidEdit.getText().toString(), passEdit.getText().toString());

                finish();
            }
        });

        btnCloseApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.exit(1);
                finish();
            }
        });
    }
}
