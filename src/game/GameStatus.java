package game;

import java.io.Serializable;
import java.util.List;

public class GameStatus implements Serializable {
    private final List<Player> playerList;

    public GameStatus(Game game){
        playerList = game.getPlayerList();
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    @Override
    public String toString(){
        StringBuilder debug = new StringBuilder(String.valueOf(this.hashCode()));
        for(Player p : getPlayerList()){
            debug.append(" ").append(p.getCurrentCell());
        }
        return debug.toString();
    }

}
