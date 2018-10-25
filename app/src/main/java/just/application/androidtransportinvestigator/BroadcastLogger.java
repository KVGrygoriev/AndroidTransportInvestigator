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
     * Used for determinate message level
     */
    public final static String LOGGER_LVL  = "LoggerLevel";

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

    private void Log(String tag, Defines.LogLevel lvl, String msg) {
        if (null == loggerIntend
            || null == contenx) {
            Log.e(tag, "WidgetLogger is not inited!");
            return;
        }

        loggerIntend.putExtra(LOGGER_TAG, tag);
        loggerIntend.putExtra(LOGGER_LVL, lvl);
        loggerIntend.putExtra(LOGGER_MSG, msg);

        contenx.sendBroadcast(loggerIntend);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public void Verbose(String tag, String msg) {
        Log(tag, Defines.LogLevel.VERBOSE, msg);
    }

    /**
     * Send a {@link #DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public void Debug(String tag, String msg) {
        Log(tag, Defines.LogLevel.DEBUG, msg);
    }

    /**
     * Send a {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public void Info(String tag, String msg) {
        Log(tag, Defines.LogLevel.INFO, msg);
    }

    /**
     * Send a {@link #WARNING} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public void Warning(String tag, String msg) {
        Log(tag, Defines.LogLevel.WARNING, msg);
    }

    /**
     * Send a {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public void Error(String tag, String msg) {
        Log(tag, Defines.LogLevel.ERROR, msg);
    }
}
