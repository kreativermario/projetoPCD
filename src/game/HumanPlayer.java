package game;

public class HumanPlayer extends Player{
        public HumanPlayer(int id, Game game, byte strength) {
            super(id, game, strength);
            System.err.println("Created Human " + super.toString());
        }

        public boolean isHumanPlayer() {
            return true;
        }
}
