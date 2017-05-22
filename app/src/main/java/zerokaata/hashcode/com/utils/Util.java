package zerokaata.hashcode.com.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by hrawat on 17-05-2017.
 */

public class Util {

    public static void showToast(Context ctx , String msg){
        Toast.makeText(ctx , msg ,Toast.LENGTH_SHORT).show();
    }

    public static boolean isBluetoothSupported() {
        boolean result ;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        result = mBluetoothAdapter == null ? false : true;
        return result;
    }
}
