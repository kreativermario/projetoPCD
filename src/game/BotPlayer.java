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
		super.initializeLocation();
		try {
			Thread.sleep(Game.INITIAL_WAITING_TIME);
			while(true){
				Thread.sleep(Game.REFRESH_INTERVAL);
				move();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}


	}


	@Override
	public void move(){
		Direction newDirection = VALUES.get(RANDOM.nextInt(SIZE));
		Coordinate directionVector = newDirection.getVector();
		Cell currentCell = super.getCurrentCell();
		Coordinate newCoordinate = currentCell.getPosition().translate(directionVector);
		System.out.println("BOT " + super.getIdentification()  + "New coordinates -> " + newCoordinate);
		// Se as novas coordenadas não forem válidas, não mexer
		if(Coordinate.isValid(newCoordinate)){
			try{
				currentCell.removePlayer();
				Cell newCell = game.getCell(newCoordinate);
				System.out.println("BOT " + super.getIdentification()  + "New cell -> " + newCell.getPosition());
				// Se a posição já estiver ocupada
				if(newCell.isOcupied()) return;
				newCell.setPlayer(this);
				game.notifyChange();

			}catch (InterruptedException e){
				e.printStackTrace(); //TODO tratar
			}
		}
	}

	@Override
	public boolean isHumanPlayer() {
		return false;
	}

}
