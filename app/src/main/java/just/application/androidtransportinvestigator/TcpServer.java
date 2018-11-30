package just.application.androidtransportinvestigator;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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


    class TcpHandler implements Runnable {

        private static final String TAG = "TcpHandler";

        private Socket clientSocket;
        private PrintWriter sendBuffer;
        private BufferedReader receiveBuffer;
        private String lastReceivedCommand;

        BroadcastLogger broadcastLogger;

        /**
         * Caller Activity/Service context
         */
        private Context context = null;

        /**
         * Constructor of the class.
         *
         * @param clientSocket    connected clients socket
         */
        public TcpHandler(Socket clientSocket, Context context) {

            this.clientSocket = clientSocket;
            this.context = context;

            broadcastLogger = new BroadcastLogger(this.context, Defines.BroadcastLoggerId.MAIN_ACTIVITY_LOGGER.toString());

            try {
                //sends the message to the server
                sendBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())), true);

                //receives the message which the client sends
                receiveBuffer = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                broadcastLogger.Error(TAG, "Error " + e.getStackTrace());
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    lastReceivedCommand = receiveBuffer.readLine();

                    if (null != lastReceivedCommand) {
                        broadcastLogger.Info(TAG, "Received msg: " + lastReceivedCommand);
                        //TODO message handler
                    }
                } catch (IOException e) {
                    broadcastLogger.Error(TAG, "Error " + e.getStackTrace());
                }
            }
        }

        /**
         * Sends the message entered by client to the server
         *
         * @param message text entered by client
         */
        public void sendMessage(final String message) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (null != sendBuffer) {
                        broadcastLogger.Debug(TAG, "Sending: " + message);
                        sendBuffer.println(message);
                        sendBuffer.flush();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}