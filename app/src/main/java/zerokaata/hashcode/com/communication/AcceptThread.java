package zerokaata.hashcode.com.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by hrawat on 19-05-2017.
 */

public class AcceptThread extends Thread {

    private static final String TAG = "aThread";
    private BluetoothServerSocket mmServerSocket;
    private BluetoothClientListener listener;

    public AcceptThread(BluetoothClientListener listener , String uuid) {
        this.listener = listener;
        BluetoothServerSocket tmp = null;
        try {
            tmp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("test111", UUID.fromString(uuid));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmServerSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;

        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                //TODO Connection successful . Manage the client socket
                listener.onClientConnected(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void cancel() {
//        try {
//            mmServerSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not close the connect socket", e);
//        }

    }

    public interface BluetoothClientListener{
        void onClientConnected(BluetoothSocket clientSocket);
    }
}
