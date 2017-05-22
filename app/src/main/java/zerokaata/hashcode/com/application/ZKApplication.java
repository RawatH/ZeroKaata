package zerokaata.hashcode.com.application;

import android.app.Application;

import zerokaata.hashcode.com.model.GameBoardVO;
import zerokaata.hashcode.com.utils.GameManager;

/**
 * Created by hrawat on 19-05-2017.
 */

public class ZKApplication extends Application {


    private GameManager gameManager;

    @Override
    public void onCreate() {
        super.onCreate();
        gameManager = GameManager.getGameManager();
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

}
