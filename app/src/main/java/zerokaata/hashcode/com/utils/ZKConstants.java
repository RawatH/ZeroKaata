package zerokaata.hashcode.com.utils;

import android.bluetooth.BluetoothProfile;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hrawat on 18-05-2017.
 */

public class ZKConstants {

    public static final int RESULT_CANCELED = 0;
    public static final int REQ_CODE_DIS_MODE = 333;

    public static final int REQUEST_ENABLE_BT = 1;

    public static final int DISCOVERY_TIME = 30;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PLAYER.TYPE_X, PLAYER.TYPE_O})
    public @interface PLAYER {

        int TYPE_O = 1;
        int TYPE_X = 2;
    }

    public static final int MSG_READ = 111;
    public static final int MSG_WRITE = 112;

    public static final String MARK_X = "X";
    public static final String MARK_O = "O";


}
