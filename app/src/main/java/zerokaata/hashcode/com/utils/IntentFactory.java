package zerokaata.hashcode.com.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.telecom.ConnectionService;

import zerokaata.hashcode.com.bluetoothconn.ConnService;

/**
 * Created by hrawat on 18-05-2017.
 */

public class IntentFactory {

    private static IntentFactory intentFactory;

    private IntentFactory() {

    }

    public static IntentFactory getInstance() {
        if (intentFactory == null) {
            intentFactory = new IntentFactory();
        }
        return intentFactory;
    }

    public Intent getEnablingBluetoothIntent() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        return enableBtIntent;
    }

    public Intent getEnableDiscoverabilityIntent() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ZKConstants.DISCOVERY_TIME);
        return discoverableIntent;
    }

    public Intent getConnectionServiceIntent(Context ctx) {
        Intent intent = new Intent(ctx, ConnService.class);
        return intent;
    }
}
