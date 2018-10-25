package just.application.androidtransportinvestigator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

final class BroadcastLogger {

    /**
     * Should contain sender TAG identifier
     */
    public final static String LOGGER_TAG = "LoggerTag";
    /**
     * Message for logger
     */
    public final static String LOGGER_MSG = "LoggerMsg";

    /**
     * Message conductor
     */
    private Intent loggerIntend = null;

    /**
     * Activity/Service context
     */
    private Context contenx = null;

    /**
     * @param contenx Caller context,
     * @param broadcastIdentifier Broadcast receiver id from BroadcastLoggerId enum
     */
    BroadcastLogger(Context contenx, String broadcastIdentifier) {
        loggerIntend = new Intent(broadcastIdentifier);
        this.contenx = contenx;
    }

    public void Log(String tag, String msg) {
        if (null == loggerIntend
            || null == contenx) {
            Log.e(tag, "WidgetLogger is not inited!");
            return;
        }

        loggerIntend.putExtra(LOGGER_TAG, tag);
        loggerIntend.putExtra(LOGGER_MSG, msg);

        contenx.sendBroadcast(loggerIntend);
    }
}
