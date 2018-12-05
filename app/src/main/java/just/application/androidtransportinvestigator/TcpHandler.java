package just.application.androidtransportinvestigator;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

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
                    messageHandler(lastReceivedCommand);
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

    private void messageHandler(final String message) {
        broadcastLogger.Info(TAG, "Received msg: " + lastReceivedCommand);

        //TODO getRPCType()
    }
}
