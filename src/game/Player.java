package game;



import coordination.CellSemaphore;
import environment.Cell;
import environment.Direction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a player.
 * @author luismota
 *
 */
public abstract class Player extends Thread{


	protected  Game game;
	private int id;
	private byte currentStrength;
	protected byte originalStrength;

	// Métodos para escolher uma direção random
	private static final List<Direction> VALUES = List.of(Direction.values());
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();


	// TODO: get player position from data in game
	public Cell getCurrentCell() {
		return game.getCellByPlayer(this);
	}

	public Player(int id, Game game, byte strength) {
		super();
		this.id = id;
		this.game=game;
		currentStrength=strength;
		originalStrength=strength;
	}

	/**
	 * Metodo utilizado para inicializar a posicao do jogador no inicio do jogo
	 */
	public void initializeLocation(){
		try {
			game.addPlayerToGame(this);
			System.err.println("Created Player " + id + " AT " + getCurrentCell().getPosition());
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run(){
		// Iniciar a posicao, se houver dead player, vai lancar uma excecao Exception
		initializeLocation();

		try {
			// Esperar que todos os players facam load
			Thread.sleep(Game.INITIAL_WAITING_TIME);
			while (true) {
				if(!isHumanPlayer()){
					// Obter a nova direcao random
					Direction newDirection = VALUES.get(RANDOM.nextInt(SIZE));
					move(newDirection);
				}
				//TODO Como fazer para mover clientes remotos?
				// Mover para humano?

				// verificar a sua energia inicial, e mover so em ciclos em que pode
				Thread.sleep(Game.REFRESH_INTERVAL*originalStrength);
			}
		} catch (InterruptedException e) {
			System.err.println("Thread: " + super.threadId() + ";  Player " + getIdentification() + " INTERRUPTED!");
			System.err.println("Player " + getIdentification()  + " -> Finished the game!");
		}
	}

	/**
	 * Torna o player num obstaculo se perdeu
	 */
	public void setObstacle(){
		this.currentStrength = 0;
		System.err.println("Player " + this.getIdentification() + " DIED!");
	}


	/**
	 * Metodo utilizado para adicionar energia ao jogador depois de uma disputa
	 * @param strength
	 */
	public void addStrength(int strength){
		if(getCurrentStrength() + strength >= 10){
			//TODO Finish game
			this.currentStrength = 10;
			game.notifyChange();
			this.interrupt();
			System.out.println("Player " + getIdentification()  + " -> Finished the game!");
			Game.countDownLatch.countDown();
			return;
		}
		this.currentStrength += strength;
	}

	public abstract void move(Direction d) throws InterruptedException;

	public abstract boolean isHumanPlayer();

	@Override
	public String toString() {
		return "[ Player " + id + " | Current Strength = " + currentStrength + " | Current Cell = " + getCurrentCell()
				+ " ] ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public byte getCurrentStrength() {
		return currentStrength;
	}


	public int getIdentification() {
		return id;
	}
}
