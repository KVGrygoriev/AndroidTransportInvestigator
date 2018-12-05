package just.application.androidtransportinvestigator;

import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable {

    private static final String TAG = "TcpServer";
    private static final int SERVER_PORT = 12346;

    private ServerSocket serverSocket;
    /**
     * Caller Activity/Service context
     */
    private Context context = null;


    TcpServer(Context context) {
        this.context = context;
    }

    public void run() {

        Socket socket = null;
        BroadcastLogger broadcastLogger = new BroadcastLogger(this.context, Defines.BroadcastLoggerId.MAIN_ACTIVITY_LOGGER.toString());

        try {
            broadcastLogger.Debug(TAG, "Server started");
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            broadcastLogger.Error(TAG, "Error " + e.getStackTrace());
        }

        while (!Thread.currentThread().isInterrupted()) {

            try {
                socket = serverSocket.accept();

                broadcastLogger.Debug(TAG, "New connection accepted "
                        + socket.getInetAddress().toString() + ":" + socket.getPort());

                TcpHandler tcpHandler = new TcpHandler(socket, context);
                new Thread(tcpHandler).start();
            } catch (IOException e) {
                broadcastLogger.Error(TAG, "Error " + e.getStackTrace());
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            broadcastLogger.Error(TAG, "Error " + e.getStackTrace());
        }
    }
}