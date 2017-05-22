package zerokaata.hashcode.com.communication;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by hrawat on 19-05-2017.
 */

public class ConnectThread extends Thread {

    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private ServerSocketListener listener;
    private static final String TAG = "cThread";

    public ConnectThread(ServerSocketListener listener, BluetoothDevice device, String uid) {
        this.listener = listener;
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(uid));
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            listener.onServerSocketReceived(mmSocket);
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        //TODO Server socket received.
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
//        try {
//            mmSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not close the client socket", e);
//        }
    }

    public interface ServerSocketListener {
        void onServerSocketReceived(BluetoothSocket serverSocket);
    }
}
