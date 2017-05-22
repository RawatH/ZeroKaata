package zerokaata.hashcode.com.model;

import android.view.View;

import zerokaata.hashcode.com.R;
import zerokaata.hashcode.com.customview.CellView;

/**
 * Created by hrawat on 19-05-2017.
 */

public class GameBoardVO {

    private static GameBoardVO instance;
    private boolean isPlayersConnected;
    private int playerType;
    private CellView.MoveListener listener;
    private View gameLayout;

    private GameBoardVO() {

    }

    public static GameBoardVO getInstance() {
        if (instance == null) {
            instance = new GameBoardVO();
        }
        return instance;

    }

    public View getGameLayout() {
        return gameLayout;
    }

    public void setGameLayout(View gameLayout) {
        this.gameLayout = gameLayout;
    }

    public CellView.MoveListener getListener() {
        return listener;
    }

    public void setListener(CellView.MoveListener listener) {
        this.listener = listener;
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

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public void updateMoveOnBoard(String cellId){

       if(gameLayout != null){
           switch (Integer.parseInt(cellId)){
               case 49:
                   ((CellView)gameLayout.findViewById(R.id.cell_one)).updateMove();
                   break;
               case 50:
                   ((CellView)gameLayout.findViewById(R.id.cell_two)).updateMove();
                   break;
               case 51:
                   ((CellView)gameLayout.findViewById(R.id.cell_three)).updateMove();
                   break;
               case 52:
                   ((CellView)gameLayout.findViewById(R.id.cell_four)).updateMove();
                   break;
               case 53:
                   ((CellView)gameLayout.findViewById(R.id.cell_five)).updateMove();
                   break;
               case 54:
                   ((CellView)gameLayout.findViewById(R.id.cell_six)).updateMove();
                   break;
               case 55:
                   ((CellView)gameLayout.findViewById(R.id.cell_seven)).updateMove();
                   break;
               case 56:
                   ((CellView)gameLayout.findViewById(R.id.cell_eight)).updateMove();
                   break;
               case 57:
                   ((CellView)gameLayout.findViewById(R.id.cell_nine)).updateMove();
                   break;

           }

       }

    }

    public void resetBoard(){
        ((CellView)gameLayout.findViewById(R.id.cell_one)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_two)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_three)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_four)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_five)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_six)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_seven)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_eight)).reset();
        ((CellView)gameLayout.findViewById(R.id.cell_nine)).reset();

    }

    @Override
    public String toString() {
        return "GameBoardVO{" +
                "isPlayersConnected=" + isPlayersConnected +
                '}';
    }
}
