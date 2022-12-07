package game;

import environment.Direction;
import gui.BoardJComponent;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class HumanPlayer extends Player implements Serializable {

    private static AtomicInteger idCounter = new AtomicInteger();
    public static int INITIAL_STRENGTH = 5;

    public HumanPlayer(Game game) {
        super(createID(), game, (byte) INITIAL_STRENGTH);
        System.err.println("Created Human " + super.toString());
    }

    public void clearMoveDirection(){
        super.setMoveDirection(null);
    }

    /**
     * Cria um id do Jogador único
     * @return
     */

    private static int createID() {
        return idCounter.getAndIncrement();
    }

    @Override
    public void run() {
        //Iniciar a posição
        super.initializeLocation();

        try{
            while(true){
                //TODO Alterar
                Direction direction = super.getMoveDirection();
                if(direction != null){
                    move(direction);
                    clearMoveDirection();
                }

            }
        }catch ( InterruptedException e){
            e.printStackTrace();
        }


    }

    @Override
    public void move(Direction d) throws InterruptedException {

    }

    public boolean isHumanPlayer() {
        return true;
    }
}

