package zerokaata.hashcode.com.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.adapter.BluetoothDeviceAdapter;
import zerokaata.hashcode.com.application.ZKApplication;
import zerokaata.hashcode.com.communication.AcceptThread;
import zerokaata.hashcode.com.communication.ConnectThread;
import zerokaata.hashcode.com.communication.DataTransferThread;
import zerokaata.hashcode.com.customview.IndicatorView;
import zerokaata.hashcode.com.listener.AlertSelectionListener;
import zerokaata.hashcode.com.model.GameBoardVO;
import zerokaata.hashcode.com.utils.GameManager;
import zerokaata.hashcode.com.utils.IntentFactory;
import zerokaata.hashcode.com.utils.Util;
import zerokaata.hashcode.com.utils.ZKConstants;


public class ServerClientFragment extends Fragment implements IndicatorView.PlayModeSelectionListener, AlertSelectionListener,
        AcceptThread.BluetoothClientListener, ConnectThread.ServerSocketListener, GameBoardVO.PlayerMoveListener {

    private TextView bannerTxt;
    private Context ctx;
    private IndicatorView serverBtn;
    private IndicatorView clientBtn;
    private TextView cancel;

    private Messenger messenger = new Messenger(new IncomingHandler());

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private DataTransferThread dataTransferThread;

    private BluetoothSocket bluetoothSocket;

    private final static int REQUEST_CODE_LOC = 2;
    private View gameViewStub;
    private View scoreViewStub;
    private View gameControlStub;
    private View boardInfatedView;

    private View scoreInflatedView;
    private View gameInflatedView;

    private TextView yourScore;
    private TextView opponentScore;
    private TextView yourName;
    private TextView opponentName;

    private ImageView yourTrophy;
    private ImageView oppTrophy;
    private ImageView defTrophy;

    private LinearLayout oppScoreContainer;
    private LinearLayout userScoreContainer;

    private ZKApplication application;
    private AlertDialog closeDialog;

    private static final String TAG = ServerClientFragment.class.getSimpleName();

    private GameManager gameManager;


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String data = bundle.getString("data");
            switch (msg.what) {
                case ZKConstants.MSG_READ:
                    Log.d(TAG, "Msg Read : " + data);
                    if (data.indexOf("reset") == 2) {
                        gameManager.resetGame(getActivity(), false);
                        resetScoreBoardUIState();

                    } else if (data.indexOf("quit") == 2) {
                        showClosingAlert(getString(R.string.quitMsg));
                    } else {
                        gameManager.updateOpponentMove(data);
                    }
                    break;

                case ZKConstants.MSG_WRITE:
                    Log.d(TAG, "Msg Write : " + data);

                    break;
            }
        }
    }


    public ServerClientFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_client, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        application = (ZKApplication) getActivity().getApplication();
        gameManager = application.getGameManager();

        gameViewStub = view.findViewById(R.id.gameStub);
        scoreViewStub = view.findViewById(R.id.scoreStub);
        gameControlStub = view.findViewById(R.id.gameControlsStub);
        bannerTxt = (TextView) view.findViewById(R.id.bannerTxt);
        serverBtn = (IndicatorView) view.findViewById(R.id.bserver);
        serverBtn.setListener(this);
        clientBtn = (IndicatorView) view.findViewById(R.id.bclient);
        clientBtn.setListener(this);

        cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientBtn.reset();
                serverBtn.reset();
                clientBtn.setVisibility(View.VISIBLE);
                serverBtn.setVisibility(View.VISIBLE);
                bannerTxt.setText(getString(R.string.connect_as));
            }
        });
        Typeface custom_font = Typeface.createFromAsset(ctx.getAssets(), "orange_juice.ttf");
        bannerTxt.setTypeface(custom_font);
        cancel.setTypeface(custom_font);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ctx.registerReceiver(mReceiver, filter);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ctx.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPlayModeSelected(int playMode) {
        switch (playMode) {
            case R.id.bserver:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameManager.setPlayerType(ZKConstants.PLAYER.TYPE_O);
                        bannerTxt.setText("Waiting for X ...");
                        clientBtn.setVisibility(View.GONE);
                        startActivityForResult(IntentFactory.getInstance().getEnableDiscoverabilityIntent(), ZKConstants.REQ_CODE_DIS_MODE);
                    }
                });


                break;
            case R.id.bclient:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameManager.setPlayerType(ZKConstants.PLAYER.TYPE_X);
                        bannerTxt.setText("Looking for 0 ...");
                        serverBtn.setVisibility(View.GONE);
                        accessLocationPermission();
                    }
                });

                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("res", "--" + resultCode);
        switch (resultCode) {

            case ZKConstants.DISCOVERY_TIME:
                Util.showToast(ctx, "Accepted");
                acceptThread = new AcceptThread(this, getString(R.string.uuid));
                acceptThread.start();
                break;
            case ZKConstants.RESULT_CANCELED:
                Util.showToast(ctx, "Denied");
                cancel.performClick();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void accessLocationPermission() {
        int accessCoarseLocation = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE_LOC);
        } else {
            showScannedBluetoothDevList();
        }
    }

    private AlertDialog alertDialog;
    private BluetoothDeviceAdapter alertAdapter;

    private void showScannedBluetoothDevList() {
        Typeface custom_font = Typeface.createFromAsset(ctx.getAssets(), "orange_juice.ttf");

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        LinearLayout container = new LinearLayout(ctx);
        builder.setView(container);
        alertDialog = builder.create();

        View layout = inflater.inflate(R.layout.bluetooth_list_layout, container);


        View titleView = alertDialog.getLayoutInflater().inflate(R.layout.alert_header, null);
        alertDialog.setCustomTitle(titleView);

        ((TextView) titleView.findViewById(R.id.alert_title)).setTypeface(custom_font);


        RecyclerView list = (RecyclerView) layout.findViewById(R.id.bluettoh_dev_list);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(ctx));


        alertAdapter = new BluetoothDeviceAdapter(ctx, this);
        list.setAdapter(alertAdapter);
        alertDialog.show();


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1)) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Util.showToast(context, "Discovery ON");
                        serverBtn.setShowDiscoveryTime(true);
                        serverBtn.setTimeLeft(ZKConstants.DISCOVERY_TIME);
                        cancel.setEnabled(false);

                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Util.showToast(context, "Discovery OFF");
                        serverBtn.setShowDiscoveryTime(false);
                        serverBtn.setTimeLeft(0);
                        cancel.setEnabled(true);
                        clientBtn.reset();
                        serverBtn.reset();
                        clientBtn.setVisibility(View.VISIBLE);
                        serverBtn.setVisibility(View.VISIBLE);
                        if (!gameManager.isPlayersConnected()) {
                            cancel.performClick();
                        }
                        break;
                }
            }

        }
    };


    @Override
    public void onAlertSelectionDone(BluetoothDevice bluetoothDevice) {
        Util.showToast(ctx, bluetoothDevice.getName());
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        clientBtn.reset();
        serverBtn.reset();
        clientBtn.setVisibility(View.VISIBLE);
        serverBtn.setVisibility(View.VISIBLE);
        bannerTxt.setText("Connecting with " + bluetoothDevice.getName());

        connectThread = new ConnectThread(this, bluetoothDevice, getString(R.string.uuid));
        connectThread.start();

    }

    /*The Bluetooth client has connected . Client BluetoothSocket received in the callback.
     */
    @Override
    public void onClientConnected(final BluetoothSocket clientSocket) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clientBtn.reset();
                serverBtn.reset();
                clientBtn.setVisibility(View.GONE);
                serverBtn.setVisibility(View.GONE);

                bluetoothSocket = clientSocket;
                bannerTxt.setText("Connected with client");
                acceptThread.cancel();
                gameManager.setPlayersConnected(true);
                gameManager.setListener(ServerClientFragment.this);
                boardInfatedView = ((ViewStub) gameViewStub).inflate();
                initiateDataThread();
                inflateScoreBardUI();
            }
        });

    }

    /*Server has connected with client. Server BluetoothSocket received in the callback.
     */
    @Override
    public void onServerSocketReceived(final BluetoothSocket serverSocket) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clientBtn.reset();
                serverBtn.reset();
                clientBtn.setVisibility(View.GONE);
                serverBtn.setVisibility(View.GONE);

                bluetoothSocket = serverSocket;
                bannerTxt.setText("Connected with server");
                connectThread.cancel();
                gameManager.setPlayersConnected(true);
                gameManager.setListener(ServerClientFragment.this);
                boardInfatedView = ((ViewStub) gameViewStub).inflate();
                initiateDataThread();
                inflateScoreBardUI();
            }
        });

    }

    private void inflateScoreBardUI() {
        cancel.setVisibility(View.GONE);
        bannerTxt.setVisibility(View.GONE);

        scoreInflatedView = ((ViewStub) scoreViewStub).inflate();
        gameInflatedView = ((ViewStub) gameControlStub).inflate();

        CardView closeGame = (CardView) gameInflatedView.findViewById(R.id.close_container);
        closeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClosingAlert(getString(R.string.quitChoice));
            }
        });
        CardView resetGame = (CardView) gameInflatedView.findViewById(R.id.reset_container);
        resetGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameManager.resetGame(getActivity(), true)) {
                    resetScoreBoardUIState();

                    try {
                        JSONObject data = Util.getMessage(ZKConstants.MSG_RESET, null);
                        dataTransferThread.write(data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        yourScore = (TextView) scoreInflatedView.findViewById(R.id.your_score);
        opponentScore = (TextView) scoreInflatedView.findViewById(R.id.opponent_score);

        yourName = (TextView) scoreInflatedView.findViewById(R.id.your_name);
        opponentName = (TextView) scoreInflatedView.findViewById(R.id.opponent_name);

        yourName.setTypeface(Util.getScoreTypeface(getActivity()));
        yourScore.setTypeface(Util.getScoreTypeface(getActivity()));
        opponentName.setTypeface(Util.getScoreTypeface(getActivity()));
        opponentScore.setTypeface(Util.getScoreTypeface(getActivity()));

        yourName.setText(Util.getPlayerSymbol(gameManager.getPlayerType()) + " : " + getString(R.string.default_user_name));

        opponentName.setText(Util.getOpponentSymbol(gameManager.getPlayerType()) + " : " + getString(R.string.default_opponent_name));

        yourTrophy = (ImageView) scoreInflatedView.findViewById(R.id.user_trophy);
        oppTrophy = (ImageView) scoreInflatedView.findViewById(R.id.opponent_trophy);
        defTrophy = (ImageView) scoreInflatedView.findViewById(R.id.def_trophy);

        oppScoreContainer = (LinearLayout) scoreInflatedView.findViewById(R.id.oppScoreContainer);
        userScoreContainer = (LinearLayout) scoreInflatedView.findViewById(R.id.userScoreContainer);


    }

    private void showClosingAlert(final String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.quit_alert_layout, null);
        dialogBuilder.setView(dialogView);

        TextView messageTxt = (TextView) dialogView.findViewById(R.id.alertTxt);
        messageTxt.setTypeface(Util.getScoreTypeface(getActivity()));
        messageTxt.setText(msg);

        Button quitBtn = (Button) dialogView.findViewById(R.id.quit);
        quitBtn.setTypeface(Util.getScoreTypeface(getActivity()));


        Button dntQuit = (Button) dialogView.findViewById(R.id.dntQuit);
        dntQuit.setTypeface(Util.getScoreTypeface(getActivity()));


        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(msg.equalsIgnoreCase(getString(R.string.quitChoice))) {
                    try {
                        JSONObject jsonData = Util.getMessage(ZKConstants.MSG_QUIT, null);
                        dataTransferThread.write(jsonData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ((HomeActivity)getActivity()).shutDownActivity();
            }
        });


        dntQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeDialog != null) {
                    closeDialog.dismiss();
                }
            }
        });

        if(msg.equalsIgnoreCase(getString(R.string.quitMsg))){
         dntQuit.setVisibility(View.GONE);
            quitBtn.setText(getString(R.string.close));
        }

        closeDialog = dialogBuilder.create();
        closeDialog.show();
    }

    private void initiateDataThread() {
        gameManager.setGameBoardLayout(boardInfatedView);
        dataTransferThread = new DataTransferThread(bluetoothSocket, messenger);
        dataTransferThread.start();
    }


    /**
     * Move made by player. Send message to the other player.
     *
     * @param row
     * @param col
     */
    @Override
    public void onPlayerMove(int position, int row, int col) {
        if (dataTransferThread != null) {
            try {
                int[] arr = new int[3];
                arr[0] = row;
                arr[1] = col;
                arr[2] = position;
                JSONObject data = Util.getMessage(ZKConstants.MSG_WRITE, arr);
                dataTransferThread.write(data.toString());
            } catch (JSONException jse) {
                jse.printStackTrace();
            }

        }
    }

    @Override
    public void updateScoreBoard(int userScore, int oppScore) {

        yourScore.setText(String.valueOf(userScore));
        opponentScore.setText(String.valueOf(oppScore));

        if (userScore == oppScore) {
            yourTrophy.setVisibility(View.INVISIBLE);
            oppTrophy.setVisibility(View.INVISIBLE);
            defTrophy.setVisibility(View.VISIBLE);
            return;
        }
        defTrophy.setVisibility(View.INVISIBLE);
        if (userScore > oppScore) {
            yourTrophy.setVisibility(View.VISIBLE);
            oppTrophy.setVisibility(View.INVISIBLE);
        } else {
            yourTrophy.setVisibility(View.INVISIBLE);
            oppTrophy.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void updateNameOnBoard(String userName, String oppName) {
        yourName.setText(userName);
        opponentName.setText(oppName);
    }

    @Override
    public void updateTurnOnUI(boolean isOpponentTurn) {

        if (isOpponentTurn) {
            userScoreContainer.setBackgroundColor(Color.WHITE);
            oppScoreContainer.setBackgroundColor(getResources().getColor(R.color.turn));
            yourScore.setTextColor(getResources().getColor(R.color.userCol));
            yourName.setTextColor(getResources().getColor(R.color.userCol));
            opponentScore.setTextColor(Color.WHITE);
            opponentName.setTextColor(Color.WHITE);


        } else {
            userScoreContainer.setBackgroundColor(getResources().getColor(R.color.turn));
            oppScoreContainer.setBackgroundColor(Color.WHITE);
            yourScore.setTextColor(Color.WHITE);
            yourName.setTextColor(Color.WHITE);
            opponentScore.setTextColor(getResources().getColor(R.color.oppColor));
            opponentName.setTextColor(getResources().getColor(R.color.oppColor));


        }
    }

    private void resetScoreBoardUIState() {
        userScoreContainer.setBackgroundColor(Color.WHITE);
        oppScoreContainer.setBackgroundColor(Color.WHITE);
        yourScore.setTextColor(getResources().getColor(R.color.userCol));
        yourName.setTextColor(getResources().getColor(R.color.userCol));
        opponentScore.setTextColor(getResources().getColor(R.color.oppColor));
        opponentName.setTextColor(getResources().getColor(R.color.oppColor));
    }

    public interface ActivityInterface{
        void shutDownActivity();
    }


}
