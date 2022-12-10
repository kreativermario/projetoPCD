package game;

import environment.Cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameStatus implements Serializable {
    private List<Player> playerList = new ArrayList<Player>();

    public GameStatus(Game game){
        Cell[][] board = game.getBoard();
        for (int x = 0; x < Game.DIMX; x++) {
            for (int y = 0; y < Game.DIMY; y++) {
                Player player = board[x][y].getPlayer();
                if (player != null) {
                    playerList.add(player);
                }
            }
        }
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    @Override
    public String toString(){
        String debug = String.valueOf(this.hashCode());
        for(Player p : getPlayerList()){
            debug += " " + p.getCurrentCell();
        }
        return debug;
    }

}
