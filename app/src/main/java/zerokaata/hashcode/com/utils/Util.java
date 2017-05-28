package zerokaata.hashcode.com.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

/**
 * Created by hrawat on 17-05-2017.
 */

public class Util {

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isBluetoothSupported() {
        boolean result;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        result = mBluetoothAdapter == null ? false : true;
        return result;
    }

    public static String getPlayerSymbol(int playerType) {
        if (playerType == ZKConstants.PLAYER.TYPE_X) {
            return ZKConstants.MARK_X;
        }
        if (playerType == ZKConstants.PLAYER.TYPE_O) {
            return ZKConstants.MARK_O;
        }
        return null;
    }

    public static String getOpponentSymbol(int playerType) {
        if (playerType == ZKConstants.PLAYER.TYPE_X) {
            return ZKConstants.MARK_O;
        }
        if (playerType == ZKConstants.PLAYER.TYPE_O) {
            return ZKConstants.MARK_X;
        }
        return null;
    }

    public static int getOpponentPlayerType(int playerType) {
        if (playerType == ZKConstants.PLAYER.TYPE_X) {
            return ZKConstants.PLAYER.TYPE_O;
        }
        if (playerType == ZKConstants.PLAYER.TYPE_O) {
            return ZKConstants.PLAYER.TYPE_X;
        }
        return -1;
    }

    public static String getJSONString(byte[] buffer) {

        StringBuffer sb = new StringBuffer();
        for (byte b : buffer) {
            sb.append((char) b);
            if (b == 125) {
                break;
            }
        }
        return sb.toString();
    }


    public static JSONObject getMessage(int msgType, int dataArr[]) throws JSONException {

        JSONObject data = new JSONObject();
        switch (msgType) {
            case ZKConstants.MSG_WRITE:
                data.put("rowId", dataArr[0]);
                data.put("colId", dataArr[1]);
                data.put("position", dataArr[2]);
                return data;

            case ZKConstants.MSG_RESET:
                data.put("reset", ZKConstants.MSG_RESET);
                return data;

            case ZKConstants.MSG_QUIT:
                data.put("quit", ZKConstants.MSG_QUIT);
                return data;

        }


        return null;
    }

    public static Typeface getScoreTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "score_font.ttf");

    }

    public static boolean canOppWin(int gameArr[] , int oppPlayerType ){


        for(String winLine : ZKConstants.WINMATCHLIST){
            StringTokenizer st = new StringTokenizer(winLine , ":");
            int posArr[] = new int[3];
            int counter = 0;
            while (st.hasMoreTokens()){
              posArr[counter++] = Integer.valueOf(st.nextToken());
            }
            if((gameArr[posArr[0]]== oppPlayerType || gameArr[posArr[0]]== 0) &&
                    (gameArr[posArr[1]]== oppPlayerType || gameArr[posArr[0]]== 0) &&
                    (gameArr[posArr[2]]== oppPlayerType || gameArr[posArr[0]]== 0) ){
                return true;
            }
        }

     return false;
    }
}
