package zerokaata.hashcode.com.bluetoothconn;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

/**
 * Created by hrawat on 18-05-2017.
 */

public class ConnService extends Service {

    private BluetoothSocket bluetoothSocket;
    private Messenger actMessenger;
    private Messenger messenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }


    public void setActMessenger(Messenger actMessenger) {
        this.actMessenger = actMessenger;
    }

    private void sendMsgToActivity(int msg){

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
