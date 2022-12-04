package game;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
     private Game game;
    @Serial
    private static final long serialVersionUID = 1399116563470735156L;

    public Message(Game game){
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
