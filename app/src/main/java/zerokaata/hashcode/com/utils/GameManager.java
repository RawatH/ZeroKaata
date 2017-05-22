package zerokaata.hashcode.com.utils;

import android.view.View;

import zerokaata.hashcode.com.customview.CellView;
import zerokaata.hashcode.com.model.GameBoardVO;

/**
 * Created by demo on 21/05/17.
 */

public class GameManager {
    private static GameManager boardManager;
    private static GameBoardVO gameBoardVO;

    public static int GAME_COUNTER = 0;

    private GameManager(){

    }

    public  static GameManager getGameManager(){
        if(boardManager == null){
            boardManager = new GameManager();
            gameBoardVO = GameBoardVO.getInstance();
        }
        return boardManager;
    }


    public void setPlayerType( int playerType){
        gameBoardVO.setPlayerType(playerType);
    }
    public int getPlayerType( ){
        return gameBoardVO.getPlayerType();
    }

    public boolean isPlayersConnected(){
        return  gameBoardVO.isPlayersConnected();
    }

    public void setPlayersConnected(boolean flag){
        gameBoardVO.setPlayersConnected(flag);
    }

    public void setListener(CellView.MoveListener listener){
        gameBoardVO.setListener(listener);
    }
    public CellView.MoveListener getListener(){
        return gameBoardVO.getListener();
    }

    public void updateMove(String cellId){

        if(gameBoardVO != null){
            GAME_COUNTER ++;
            gameBoardVO.updateMoveOnBoard(cellId);
            if(GAME_COUNTER == 9){
                GAME_COUNTER = 0;
                gameBoardVO.resetBoard();
            }
        }
    }

    public void setGameBoardLayout(View layout){

        gameBoardVO.setGameLayout(layout);

    }

}
