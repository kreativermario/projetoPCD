package game;

import environment.Cell;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    private List<Player> playerList = new ArrayList<Player>();
    private Cell[][] board;


    public Message(Game game){
        board = game.getBoard();
        for (int x = 0; x < Game.DIMX; x++) {
            for (int y = 0; y < Game.DIMY; y++) {
                Player player = board[x][y].getPlayer();
                if (player != null) {
                    playerList.add(player);
                }
            }
        }
    }

    public Cell[][] getBoard() {
        return board;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }
}
