package game;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class HumanPlayer extends Player implements Serializable {

    private static AtomicInteger idCounter = new AtomicInteger();
    public static final int INITIAL_STRENGTH = 5;

    public HumanPlayer(Game game) {
        super(createID(), game, (byte) INITIAL_STRENGTH);
        System.err.println("Created Human " + super.toString());
    }

    /**
     * Cria um id do Jogador único
     * @return
     */

    private static int createID() {
        return idCounter.getAndIncrement();
    }

    public boolean isHumanPlayer() {
        return true;
    }
}

