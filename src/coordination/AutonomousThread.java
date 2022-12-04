package coordination;

import environment.Cell;
import game.Game;
import game.Player;

public class AutonomousThread extends Thread{

    private Thread player;

    public AutonomousThread(Thread player){
        System.err.println(threadId() + " || CREATED AUTONOMOUS THREAD FOR ");
        this.player = player;
    }

    @Override
    public void run() {
        try {
            // Aguardar 2 segundos
            System.err.println(threadId() + " || WAITING 2 SECS FOR " );
            sleep(Game.MAX_WAITING_TIME_FOR_MOVE);
            // Notificar a thread player
            player.interrupt();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
