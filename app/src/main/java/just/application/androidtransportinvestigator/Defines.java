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

    enum BroadcastLoggerId {
        /**
         * Broadcast logger id, which allow to replay messages in loger-widget in main activity
         */
        MAIN_ACTIVITY {
            public String toString() {
                return "MAIN_ACTIVITY_BROADCAST_WIDGET_LOGGER";
            }
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
}