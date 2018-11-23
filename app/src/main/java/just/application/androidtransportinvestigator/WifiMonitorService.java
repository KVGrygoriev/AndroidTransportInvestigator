package just.application.androidtransportinvestigator;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.util.Timer;
import java.util.TimerTask;

import static just.application.androidtransportinvestigator.Defines.MILISEC_IN_SEC;
import static just.application.androidtransportinvestigator.Defines.NetworkActions.ENABLE_WIFI;
import static just.application.androidtransportinvestigator.Defines.NetworkActions.START_TCP;

/**
 * A {@link WifiMonitorService } class for monitoring WIFI network status
 */
public class WifiMonitorService extends IntentService {

    /**
     * Used for determinate message level
     */
    public final static String NETWORK_ACTIONS = "NetworkActions";
    private Intent wifiMonitorIntend;

    private Timer timer = new Timer();

    public WifiMonitorService() {
        super("WifiMonitorService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        wifiMonitorIntend = new Intent(Defines.BroadcastLoggerId.WIFI_MONITOR.toString());

        timer = new Timer();
        timer.schedule(new WifiChecker(), 0, 5 * MILISEC_IN_SEC);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        timer.cancel();
        timer.purge();

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    class WifiChecker extends TimerTask {
        @Override
        public void run() {

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if ( WifiManager.WIFI_STATE_ENABLED != wifiManager.getWifiState()) {
                //TODO check is connection established, not only enabled
                wifiMonitorIntend.putExtra(NETWORK_ACTIONS, ENABLE_WIFI);
            } else {
                wifiMonitorIntend.putExtra(NETWORK_ACTIONS, START_TCP);
            }

            sendBroadcast(wifiMonitorIntend);
        }
    }
}
