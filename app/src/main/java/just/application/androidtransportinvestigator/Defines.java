package just.application.androidtransportinvestigator;

class Defines {
    enum TransportType {
        NONE,
        MBT,
        LBT,
        USB,
        TCP
    };

    public final static String BT_TYPE_KEY = "BtType";
    public final static String BT_SECURITY_LVL_KEY = "BtSecurityLevel";

    public final static String TCP_IP_KEY = "UserIp";

    public final static int MILISEC_IN_SEC = 1000;


    enum BroadcastLoggerId {
        /**
         * Broadcast logger id, which allow to replay messages in loger-widget in main activity
         */
        MAIN_ACTIVITY_LOGGER {
            public String toString() {
                return "MAIN_ACTIVITY_BROADCAST_WIDGET_LOGGER";
            }
        },
        /**
         * Broadcast WIFI monitor id, notifying user about problems with WIFI network
         */
        WIFI_MONITOR {
            public String toString() { return "WIFI_MONITOR_BROADCAST"; }
        }
    };

    /**
     * Used for determinate broadcast logger message level
     */
    enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }

    enum NetworkActions {
        ENABLE_WIFI,
        START_TCP
    }
}