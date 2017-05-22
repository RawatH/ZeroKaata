package zerokaata.hashcode.com.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.listener.AlertSelectionListener;
import zerokaata.hashcode.com.utils.Util;

/**
 * Created by hrawat on 02-05-2017.
 */

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BViewHolder> {

    private ArrayList<BluetoothDevice> deviceArrayList = new ArrayList<>();
    private AlertSelectionListener listener;
    private Context ctx;


    public BluetoothDeviceAdapter(Context ctx, AlertSelectionListener listener) {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceArrayList.add(device);
            }
        }
        this.listener = listener;
        this.ctx = ctx;
        registerReceiver();
        BluetoothAdapter.getDefaultAdapter().startDiscovery();

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!checkIfAlreadyExists(device)) {
                    deviceArrayList.add(device);
                    notifyDataSetChanged();
                }
            }

            if (action.contains("STARTED")) {
                Util.showToast(ctx, "Scanning devices.");
            }
            if (action.contains("FINISHED")) {
                Util.showToast(ctx, "Scanning finished.");
            }

        }
    };

    private boolean checkIfAlreadyExists(BluetoothDevice scannedDevice) {

        for (BluetoothDevice device : deviceArrayList) {
            if (device.getAddress().equalsIgnoreCase(scannedDevice.getAddress())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_dev_cell, parent, false);
        return new BViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BViewHolder holder, int position) {

        final BluetoothDevice device = deviceArrayList.get(position);
        holder.deviceName.setText(device.getName());
        holder.deviceName.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlertSelectionDone(device);
                cancelDiscovery();
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }

    public static class BViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceName;


        public BViewHolder(View view) {
            super(view);
            deviceName = (TextView) view.findViewById(R.id.deviceName);
        }

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ctx.registerReceiver(mReceiver, filter);
    }

    public void cancelDiscovery() {
        ctx.unregisterReceiver(mReceiver);
    }


}
