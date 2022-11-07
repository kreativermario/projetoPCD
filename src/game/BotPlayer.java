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

	private static final List<Direction> VALUES = Collections.unmodifiableList(Arrays.asList(Direction.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();

	public BotPlayer(int id, Game game, byte strength) {
		super(id, game, strength);
	}

	@Override
	public void run(){
		// Iniciar a posicao
		super.initializeLocation();
		try {
			// Esperar que todos os players facam load
			Thread.sleep(Game.INITIAL_WAITING_TIME);
			while(true){
				// Mover
				move();
				// verificar a sua energia inicial, e mover so em ciclos em que pode
				switch(this.originalStrength){
					case 1:
						Thread.sleep(Game.REFRESH_INTERVAL);
					case 2:
						Thread.sleep(Game.REFRESH_INTERVAL*2);
					case 3:
						Thread.sleep(Game.REFRESH_INTERVAL*3);
				}
			}
		} catch (InterruptedException e) {
			System.err.println(super.toString() + " INTERRUPTED!");
			return;
		}
	}


	/**
	 * Metodo do botPlayer onde ele se mexe aleatoriamente
	 */
	@Override
	public void move() throws InterruptedException {
		// Obter a nova direcao random
		Direction newDirection = VALUES.get(RANDOM.nextInt(SIZE));
		Coordinate directionVector = newDirection.getVector();
		// Obter a sua celula atual
		Cell currentCell = super.getCurrentCell();

		// Obter a nova coordenada
		Coordinate newCoordinate = currentCell.getPosition().translate(directionVector);
		// Se as novas coordenadas não forem válidas, não mexer
		if(Coordinate.isValid(newCoordinate)){
			Cell newCell = game.getCell(newCoordinate);

			System.err.println(super.toString() + " moving to... " + newCell.getPosition());

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
			newCell.setPlayer(this);
			game.notifyChange();
		}
	}

	@Override
	public boolean isHumanPlayer() {
		return false;
	}

}
