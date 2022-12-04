package coordination;

import game.BotPlayer;
import game.Game;
import game.Player;

import java.util.Random;

import static game.Game.MAX_INITIAL_STRENGTH;

public class TestThread extends Thread{
    private Game game;

    public TestThread(Game game){
        this.game = game;
    }

    @Override
    public void run() {
        try {
            sleep(20000);
            Player player = new BotPlayer(666, game, (byte) 8);
            player.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
