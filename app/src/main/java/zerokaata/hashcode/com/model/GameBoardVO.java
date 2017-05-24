package zerokaata.hashcode.com.model;

import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.customview.CellView;
import zerokaata.hashcode.com.utils.Util;

/**
 * Created by hrawat on 19-05-2017.
 */

public class GameBoardVO implements CellView.GameListener {

    private static GameBoardVO instance;
    private boolean isPlayersConnected;
    private int playerType;

    private PlayerMoveListener playerMoveListener;
    private CellView.GameListener gameListener;

    private View gameLayout;
    public static int TOTAL_MOVES = 9;
    public static int MOVE_COUNTER = 0;
    public boolean isMyTurn = true;
    private int gameArr[] = new int[9];
    private int winArr[];



    private static final String TAG = GameBoardVO.class.getSimpleName();

    private String[] winMatchList = {"0:1:2", "3:4:5", "6:7:8",
            "0:3:6", "1:4:7", "2:5:8",
            "0:4:8", "2:4:6"};


    private GameBoardVO() {
    }

    public static GameBoardVO getInstance() {
        if (instance == null) {
            instance = new GameBoardVO();
        }
        return instance;

    }


    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public void setGameLayout(View gameLayout) {
        this.gameLayout = gameLayout;
    }


    public PlayerMoveListener getPlayerMoveListener() {
        return playerMoveListener;
    }

    public void setPlayerMoveListener(PlayerMoveListener playerMoveListener) {
        this.playerMoveListener = playerMoveListener;
    }


    public boolean isPlayersConnected() {
        return isPlayersConnected;
    }

    public void setPlayersConnected(boolean playersConnected) {
        isPlayersConnected = playersConnected;
    }

    public int getPlayerType() {
        return playerType;
    }

    public String getPlayerSymbol() {
        return Util.getPlayerSymbol(playerType);
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    /**
     * UPDATE OPPONENT MOVE
     *
     * @param data
     */
    public void updateOpponentMove(String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);
            int position = jsonObject.optInt("position");

            gameArr[position] = Util.getOpponentPlayerType(playerType);

            Log.d(TAG, "Move count : " + MOVE_COUNTER);
            if (gameLayout != null) {
                getCellViewFor(position).updateMove();
                isMyTurn = true;
            }

            MOVE_COUNTER++;

            if (MOVE_COUNTER >= 5) {
                checkForWin();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void resetBoard() {
        //reset my turn flag
        setMyTurn(false);

        //reset move counter
        MOVE_COUNTER = 0;

        //reset game arr
        for (int i = 0; i < gameArr.length; i++) {
            gameArr[i] = 0;
        }

        //reset all cells
        ((CellView) gameLayout.findViewById(R.id.cell_one)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_two)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_three)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_four)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_five)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_six)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_seven)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_eight)).reset();
        ((CellView) gameLayout.findViewById(R.id.cell_nine)).reset();
    }


    private boolean checkForWin() {
        boolean winFlag = false;
        Log.d(TAG, "Checking for WIN . ");

        StringTokenizer st;
        int arr[] = new int[3];
        for (int i = 0; i < winMatchList.length; i++) {
            String val = winMatchList[i];
            st = new StringTokenizer(val, ":");
            while (st.hasMoreTokens()) {
                arr[0] = Integer.parseInt(st.nextToken());
                arr[1] = Integer.parseInt(st.nextToken());
                arr[2] = Integer.parseInt(st.nextToken());
            }
            checkForWn(arr);
            arr[0] = 0;
            arr[1] = 0;
            arr[2] = 0;
        }

        return winFlag;
    }

    private boolean checkForWn(int arr[]) {
        if (gameArr[arr[0]] == 0 || gameArr[arr[1]] == 0 || gameArr[arr[2]] == 0) {
            return false;
        }
        if (gameArr[arr[0]] == gameArr[arr[1]] && gameArr[arr[0]] == gameArr[arr[2]]) {
            Log.d(TAG, "WIN  ~~~~~~>" + gameArr[arr[0]]);
            drawWinLine(arr);
            return true;
        }

        return false;
    }

    private void drawWinLine(int arr[]) {
        winArr = new int[3];
        for (int i = 0; i < arr.length; i++) {
            winArr[i] = arr[i];
        }
        for (int i : winArr) {
            getCellViewFor(i).drawWin();
        }

    }

    private CellView getCellViewFor(int position) {
        CellView view;
        switch (position) {
            case 0:
                view = ((CellView) gameLayout.findViewById(R.id.cell_one));
                break;
            case 1:
                view = ((CellView) gameLayout.findViewById(R.id.cell_two));
                break;
            case 2:
                view = ((CellView) gameLayout.findViewById(R.id.cell_three));
                break;
            case 3:
                view = ((CellView) gameLayout.findViewById(R.id.cell_four));
                break;
            case 4:
                view = ((CellView) gameLayout.findViewById(R.id.cell_five));
                break;
            case 5:
                view = ((CellView) gameLayout.findViewById(R.id.cell_six));
                break;
            case 6:
                view = ((CellView) gameLayout.findViewById(R.id.cell_seven));
                break;
            case 7:
                view = ((CellView) gameLayout.findViewById(R.id.cell_eight));
                break;
            case 8:
                view = ((CellView) gameLayout.findViewById(R.id.cell_nine));
                break;
            default:
                view = null;
        }
        return view;
    }

    @Override
    public String toString() {
        return "GameBoardVO{" +
                "isPlayersConnected=" + isPlayersConnected +
                '}';
    }

    /**
     * GameListener callbacks
     * PLAYER MOVE
     */

    @Override
    public void updateMove(int position, int row, int col, boolean isOpponentsMove) {
        isMyTurn = false;
        MOVE_COUNTER++;
        gameArr[position] = playerType;
        playerMoveListener.onPlayerMove(position, row, col);
        if (MOVE_COUNTER >= 5) {
            checkForWin();
        }
    }

    @Override
    public boolean isMyTunNow() {
        return isMyTurn;
    }

    @Override
    public int[] getWinArr() {
        return winArr;
    }


    public interface PlayerMoveListener {

        void onPlayerMove(int position, int row, int col);

    }

}
