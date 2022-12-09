package game;

import environment.Cell;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    private List<Player> playerList = new ArrayList<Player>();


    public Message(Game game){
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
}
