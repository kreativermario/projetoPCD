package game;

/**
 *
 */
public class BotPlayer extends Player{
	public BotPlayer(int id, Game game, byte strength) {
		super(id, game, strength);
		System.err.println("Created Bot " + super.toString() );
	}

	public boolean isHumanPlayer() {
		return false;
	}
}
