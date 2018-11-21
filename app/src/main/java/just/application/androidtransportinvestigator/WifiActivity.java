package just.application.androidtransportinvestigator;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static just.application.androidtransportinvestigator.Defines.TCP_IP_KEY;

public class WifiActivity extends AppCompatActivity {

    Button btnTurnOnWifi, btnCloseApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        setTitle("WIFI settings");

        InitWindowSize();

        InitWidgets();

        registerListener();
    }

    private void InitWindowSize() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width *.9), (int)(height *.55));
    }

    private void InitWidgets() {

        btnTurnOnWifi = (Button) findViewById(R.id.btnTurnOnWifiSettings);
        btnCloseApp = (Button) findViewById(R.id.btnCloseAppWifiSettings);
    }

    private void registerListener() {

        btnTurnOnWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
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
