package zerokaata.hashcode.com.utils;

import android.content.Context;
import android.view.View;

import zerokaata.hashcode.com.customview.CellView;
import zerokaata.hashcode.com.model.GameBoardVO;

/**
 * Created by demo on 21/05/17.
 */

public class GameManager {

    private static GameManager gameManager;
    private static GameBoardVO gameBoardVO;


    private GameManager() {

    }

    public static GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = new GameManager();
            gameBoardVO = GameBoardVO.getInstance();
        }
        return gameManager;
    }


    public CellView.GameListener getGameListener() {
        return gameBoardVO;
    }

    public void setPlayerType(int playerType) {
        gameBoardVO.setPlayerType(playerType);
    }

    public int getPlayerType() {
        return gameBoardVO.getPlayerType();
    }

    public String getPlayerSymbol() {
        return gameBoardVO.getPlayerSymbol();
    }

    public boolean isPlayersConnected() {
        return gameBoardVO.isPlayersConnected();
    }

    public void setPlayersConnected(boolean flag) {
        gameBoardVO.setPlayersConnected(flag);
    }

    public void setListener(GameBoardVO.PlayerMoveListener listener) {
        gameBoardVO.setPlayerMoveListener(listener);
    }

    public GameBoardVO.PlayerMoveListener getListener() {
        return gameBoardVO.getPlayerMoveListener();
    }

    public void updateOpponentMove(String data) {

        if (gameBoardVO != null) {
            gameBoardVO.updateOpponentMove(data);
        }
    }

    public void setGameBoardLayout(View layout) {

        gameBoardVO.setGameLayout(layout);

    }

    public boolean resetGame(Context ctx ,boolean sourceUser) {
        return gameBoardVO.resetBoard(ctx, sourceUser);
    }


}
