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

    public static String getPlayerSymbol(int playerType){
        if (playerType == ZKConstants.PLAYER.TYPE_X) {
            return  ZKConstants.MARK_X;
        }
        if (playerType == ZKConstants.PLAYER.TYPE_O) {
            return  ZKConstants.MARK_O;
        }
        return null;
    }

    public static String getOpponentSymbol(int playerType){
        if (playerType == ZKConstants.PLAYER.TYPE_X) {
            return  ZKConstants.MARK_O;
        }
        if (playerType == ZKConstants.PLAYER.TYPE_O) {
            return  ZKConstants.MARK_X;
        }
        return null;
    }

    public static String getJSONString (byte []  buffer){

        StringBuffer sb = new StringBuffer();
        for(byte b : buffer){
            sb.append((char)b);
            if(b == 125 ){
                break;
            }
        }
        return sb.toString();
    }
}
