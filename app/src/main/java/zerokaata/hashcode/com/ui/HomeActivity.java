package zerokaata.hashcode.com.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.utils.IntentFactory;
import zerokaata.hashcode.com.utils.Util;
import zerokaata.hashcode.com.utils.ZKConstants;

public class HomeActivity extends AppCompatActivity implements ServerClientFragment.ActivityInterface {

    private RelativeLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        container = (RelativeLayout) findViewById(R.id.fragContainer);

        if (Util.isBluetoothSupported()) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                startActivityForResult(IntentFactory.getInstance().getEnablingBluetoothIntent(), ZKConstants.REQUEST_ENABLE_BT);
            } else {
                renderFragment(1);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZKConstants.REQUEST_ENABLE_BT:
                Log.d("res","---"+data);
                if(resultCode == -1){
                    renderFragment(1);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void renderFragment(int id) {
        switch (id) {
            case 1:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.fragContainer, new ServerClientFragment(), null);
                ft.commitAllowingStateLoss();
                break;
            default:
                break;

        }

    }


    @Override
    public void shutDownActivity() {

        finish();
    }
}
