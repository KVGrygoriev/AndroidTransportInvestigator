package just.application.androidtransportinvestigator;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    private static final String TAG = "TcpClient";
    public static final String SERVER_IP = "10.42.0.1"; //TODO make more flexible
    public static final int SERVER_PORT = 9877; //TODO make more flexible

    private Socket socket;
    private PrintWriter sendBuffer;
    private BufferedReader receiveBuffer;
    private String lastReceivedCommand;
    private OnMessageReceived messageListener = null;
    private boolean isRuning = false;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener) {

        messageListener = listener;
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
                    Log.d(TAG, "Sending: " + message);
                    sendBuffer.println(message);
                    sendBuffer.flush();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        Log.d(TAG, "Client is stopping...");

        isRuning = false;

        if (null != sendBuffer) {
            sendBuffer.flush();
            sendBuffer.close();
        }

        messageListener = null;
        receiveBuffer = null;
        sendBuffer = null;
        lastReceivedCommand = null;
    }

    public void run() {

        isRuning = true;

        try {

            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d(TAG, "Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {

                //sends the message to the server
                sendBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                receiveBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                //in this while the client listens for the messages sent by the server
                while (isRuning) {

                    lastReceivedCommand = receiveBuffer.readLine();

                    if (null != lastReceivedCommand && null != messageListener) {
                        messageListener.messageReceived(lastReceivedCommand);
                    }

                }

                Log.d(TAG, "Received Message: '" + lastReceivedCommand + "'");

            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }

    }

    /**
     * Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
     * class at on AsyncTask doInBackground
     */
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}