package game;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;

/**
 *
 */
public class BotPlayer extends Player {

	public BotPlayer(int id, Game game, byte strength) {
		super(id, game, strength);
	}


	/**
	 * Metodo do botPlayer onde ele se mexe aleatoriamente
	 */
	public void move(Direction newDirection) throws InterruptedException {
		Coordinate directionVector = newDirection.getVector();
		// Obter a sua celula atual
		Cell currentCell = super.getCurrentCell();

		// Obter a nova coordenada
		Coordinate newCoordinate = currentCell.getPosition().translate(directionVector);
		// Se as novas coordenadas não forem válidas, não mexer
		if(Coordinate.isValid(newCoordinate)){
			Cell newCell = game.getCell(newCoordinate);

			//System.err.println(super.toString() + " moving to... " + newCell.getPosition());
			currentCell.transferPlayer(newCell);

			game.notifyChange();
		}
	}

	@Override
	public boolean isHumanPlayer() {
		return false;
	}

}
