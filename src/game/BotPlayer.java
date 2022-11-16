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
public class BotPlayer extends Player{

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

			//TODO Comportamento para obstaculos
			if(newCell.isObstacle()){
				System.err.println(super.toString() + " tried to move to [OBSTACLE]");
				return;
			}
			//TODO Se a posicao ja estiver ocupada chamar um metodo de fight?
			if(newCell.isOcupied()){
				newCell.fight(this);
				game.notifyChange();
				return;
			}
			currentCell.removePlayer();
			newCell.setPlayer(this, true);
			game.notifyChange();
		}
	}

	@Override
	public boolean isHumanPlayer() {
		return false;
	}

}
