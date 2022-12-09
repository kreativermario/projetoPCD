package game;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;

import java.io.Serializable;

/**
 *
 */
public class BotPlayer extends Player implements Serializable {

	public BotPlayer(int id, Game game, byte strength) {
		super(id, game, strength);
	}


	@Override
	public boolean isHumanPlayer() {
		return false;
	}

}
