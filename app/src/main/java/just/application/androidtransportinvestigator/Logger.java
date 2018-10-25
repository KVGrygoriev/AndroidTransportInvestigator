package just.application.androidtransportinvestigator;

import android.util.Log;
import android.widget.TextView;

final class Logger {

    private static void logToTextView(TextView view, String tag, String msg) {
        view.setText("[" + tag + "] " + msg + "\n" + view.getText());
    }

    /**
     * Send a {@link #VERBOSE} log message.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int Verbose(TextView view, String tag, String msg) {
        logToTextView(view, tag, msg);
        return Log.v(tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int Verbose(TextView view, String tag, String msg, Throwable tr) {
        logToTextView(view, tag, msg);
        return Log.v(tag, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int Debug(TextView view, String tag, String msg) {
        logToTextView(view, tag, msg);
        return Log.d(tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int Debug(TextView view, String tag, String msg, Throwable tr) {
        logToTextView(view, tag, msg);
        return Log.d(tag, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int Info(TextView view, String tag, String msg) {
        logToTextView(view, tag, msg);
        return Log.i(tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int Info(TextView view, String tag, String msg, Throwable tr) {
        logToTextView(view, tag, msg);
        return Log.i(tag, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int Warning(TextView view, String tag, String msg) {
        logToTextView(view, tag, msg);
        return Log.w(tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int Warning(TextView view, String tag, String msg, Throwable tr) {
        logToTextView(view, tag, msg);
        return Log.w(tag, msg, tr);
    }

    /**
     * Send an {@link #ERROR} log message.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int Error(TextView view, String tag, String msg) {
        logToTextView(view, tag, msg);
        return Log.e(tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * @param view The view for logs on the phone
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int Error(TextView view, String tag, String msg, Throwable tr) {
        logToTextView(view, tag, msg);
        return Log.e(tag, msg, tr);
    }
}
