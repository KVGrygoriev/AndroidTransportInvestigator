package just.application.androidtransportinvestigator;

import android.content.Context;

import com.google.gson.JsonObject;

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

    /**
     * Function is processing incoming RPCs
     *
     * @param incomingJsonString incoming string with RPC
     */
    private void messageHandler(final String incomingJsonString) {

        broadcastLogger.Info(TAG, "Received msg: " + lastReceivedCommand);

        JsonObject jsonObject = just.application.androidtransportinvestigator.JsonParser.parseJsonString(incomingJsonString);

        if (null == jsonObject) {
            return;
        }

        switch (getRPCType(jsonObject)) {
            case CONNECT_TO_SDL:
                broadcastLogger.Debug(TAG, "Msg type: CONNECT_TO_SDL");
              //ConnectToSDL();
              break;

            case REMOVE_CONNECTION:
                broadcastLogger.Debug(TAG, "Msg type: REMOVE_CONNECTION");
                break;

            case GET_LOIST_AVALIABLE_TRANSPORTS:
                broadcastLogger.Debug(TAG, "Msg type: GET_LOIST_AVALIABLE_TRANSPORTS");
                break;

            default:
                broadcastLogger.Debug(TAG, "Msg type: default");
                break;
        }
    }

    /**
     * Function is picking put RPC type from jsonObject
     *
     * @param jsonObject object with incoming RPC
     * @return RPC type
     */

    Defines.ATF_RPC getRPCType(final JsonObject jsonObject) {

        final String messageType = just.application.androidtransportinvestigator.JsonParser.getAsString(jsonObject, "msgType");

        if (messageType.equals(null)) {
            broadcastLogger.Error(TAG, "Can't get message type. Message: " + jsonObject.toString());
            return Defines.ATF_RPC.UNDEFINED;
        }

        if (messageType.equals("ConnectToSDL"))
            return Defines.ATF_RPC.CONNECT_TO_SDL;

        if (messageType.equals("RemoveConnection"))
            return Defines.ATF_RPC.REMOVE_CONNECTION;

        if (messageType.equals("GetListOfAvailableTransports"))
            return Defines.ATF_RPC.GET_LOIST_AVALIABLE_TRANSPORTS;

        if (messageType.equals("SendData"))
            return Defines.ATF_RPC.SEND_DATA;

        return Defines.ATF_RPC.UNDEFINED;
    }

}
