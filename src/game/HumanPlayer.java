package game;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class HumanPlayer extends Player{
        public HumanPlayer(int id, Game game, byte strength) {
            super(id, game, strength);
            System.err.println("Created Human " + super.toString());
        }

    @Override
    public void run() {
            //Iniciar a posição
       super.initializeLocation();
       /* try{
            while(true){
                //Mover
                move();
            }
        }catch ( InterruptedException e){
            e.printStackTrace();
        }*/

    }

    @Override
    public void move() {

    }

    public boolean isHumanPlayer() {
            return true;
        }
}
