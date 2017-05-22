package zerokaata.hashcode.com.communication;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import zerokaata.hashcode.com.utils.ZKConstants;

/**
 * Created by demo on 20/05/17.
 */

public class DataTransferThread extends Thread {

    private Messenger messenger;
    private BluetoothSocket bluetoothSocket;
    private InputStream inStream;
    private OutputStream outStream;
    private byte[] buffer;
    private static final String TAG = DataTransferThread.class.getSimpleName();

    public DataTransferThread(BluetoothSocket socket, Messenger messenger) {
        this.bluetoothSocket = socket;
        this.messenger = messenger;

        try {
            inStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating InputStream from socket");
        }
        try {
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating OutputStream from socket");
        }
    }

    public void run() {
        Log.d(TAG , "DataTransferThread started");

        buffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = inStream.read(buffer);
                // Send the obtained bytes to the UI activity.
                Log.e(TAG, "READ Result -- " + (int)buffer[0]);
                sendMessage(String.valueOf((int)buffer[0]) , ZKConstants.MSG_READ);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Input stream was disconnected");
               // break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(String bytes) {
        try {
            outStream.write(bytes.getBytes());

            Log.e(TAG, "Written Result -- " + bytes.toString());
            sendMessage(bytes , ZKConstants.MSG_READ);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

            // Send a failure message back to the activity.
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    private void sendMessage(String data ,int id) {
        Message msg = Message.obtain();
        Bundle bundle = msg.getData();
        msg.what = id;

        bundle.putString("data", data);
        msg.setData(bundle);

        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "RemoteException while sending message");
        }

    }
}
