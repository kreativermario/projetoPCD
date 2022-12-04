package game;

import environment.Direction;
import java.util.concurrent.atomic.AtomicInteger;

public class HumanPlayer extends Player{

    private static AtomicInteger idCounter = new AtomicInteger();

    public HumanPlayer(Game game) {
        super(createID(), game, (byte) 5);
        System.err.println("Created Human " + super.toString());
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
                move(Direction.DOWN);
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

