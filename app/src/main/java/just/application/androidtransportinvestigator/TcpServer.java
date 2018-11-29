package just.application.androidtransportinvestigator;

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

    public void run() {

        Socket socket = null;

        try {
            Log.d(TAG, "Server started");
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        }

        while (!Thread.currentThread().isInterrupted()) {

            try {
                socket = serverSocket.accept();

                TcpHandler tcpHandler = new TcpHandler(socket);
                new Thread(tcpHandler).start();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        }
    }


    class TcpHandler implements Runnable {

        private static final String TAG = "TcpHandler";

        private Socket clientSocket;
        private PrintWriter sendBuffer;
        private BufferedReader receiveBuffer;
        private String lastReceivedCommand;

        /**
         * Constructor of the class.
         *
         * @param clientSocket    connected clients socket
         */
        public TcpHandler(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {
                //sends the message to the server
                sendBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())), true);

                //receives the message which the client sends
                receiveBuffer = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    lastReceivedCommand = receiveBuffer.readLine();

                    if (null != lastReceivedCommand) {
                        Log.i(TAG, "Received msg: " + lastReceivedCommand);
                        //TODO message handler
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error", e);
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
                        Log.d(TAG, "Sending: " + message);
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