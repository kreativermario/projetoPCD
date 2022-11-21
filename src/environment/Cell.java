package environment;

import coordination.CellSemaphore;
import game.Game;
import game.Player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Cell implements Comparable<Cell>{
	private Coordinate position;
	private Game game;
	private Lock lock = new ReentrantLock();
	public Lock moveLock = new ReentrantLock();
	private Condition isFull = lock.newCondition();
	private static AtomicInteger idCounter = new AtomicInteger();

	private static AtomicInteger fightCounter = new AtomicInteger(); //TODO Debug id da fight

	private int id;
	private Player player=null;
	private boolean isObstacle;

	public Cell(Coordinate position,Game g) {
		super();
		id = createID();
		this.position = position;
		this.game=g;
	}

	public int getId(){
		return id;
	}

	private int createID() {
		return idCounter.getAndIncrement();
	}

	private int createFightID() {
		return fightCounter.getAndIncrement();
	}

	/**
	 * Obter a coordenada da celula
	 * @return position
	 */
	public Coordinate getPosition() {
		return position;
	}

	/**
	 * Devolve se a celula esta ocupada por um player
	 * @return boolean
	 */
	public boolean isOcupied() {
		return player!=null;
	}


	/**
	 * Devolve se o player que esta na cell e um obstaculo ou nao
	 * @return
	 */
	public boolean isObstacle(){
		return isObstacle;
	}

	public synchronized void setObstacle(boolean isObstacle){
		this.player.setObstacle();
		this.isObstacle = isObstacle;
	}


	/**
	 * Devolve o player que esta atualmente na celula
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}

	public void setPlayerTransfer(Player player){
		this.player = player;
	}

	public void transferPlayer(Cell to){

		int fightId = createFightID();
		System.err.println("Encounter" + fightId + " || TRANSFER BETWEEN " + this + " AND " + to);

		//TODO try locks
		if(this.compareTo(to) > 0){
			this.moveLock.lock();
			System.err.println("Encounter" + fightId + " || locked " + this);
			to.moveLock.lock();
			System.err.println("Encounter" +  fightId+ " || locked " + to);
		}else{
			to.moveLock.lock();
			System.err.println("Encounter" +  fightId + " || locked " + to);
			this.moveLock.lock();
			System.err.println("Encounter" + fightId + " || locked " + this);
		}

		//TODO locks com sucesso vou fazer disputa
		Player fromPlayer = this.getPlayer();

		if(to.isOcupied()){
			System.err.println("Encounter" + fightId + " || " + this.player + " is going to fight " + to.getPlayer()); //TODO Debug
			to.fight(fromPlayer);
			if(to.isObstacle()){

			}
		// Não tem player, então é só mover!
		}else {
			this.removePlayer();
			to.setPlayerTransfer(fromPlayer);
		}

		//TODO Acabou a disputa, dar unlock
		if(this.compareTo(to) > 0){
			this.moveLock.unlock();
			System.err.println("Encounter" + fightId + " || Unlocked " + this);
			to.moveLock.unlock();
			System.err.println("Encounter" + fightId + " || Unlocked " + to);
		}else{
			to.moveLock.unlock();
			System.err.println("Encounter" + fightId + " || Unlocked " + to);
			this.moveLock.unlock();
			System.err.println("Encounter" + fightId + " || Unlocked " + this);
		}

	}


	/**
	 * Metodo que trata a disputa entre dois players
	 * @param opponent
	 */
	private Player fight(Player opponent){

		int thisPlayerStrength = this.player.getCurrentStrength();
		int otherPlayerStrength = opponent.getCurrentStrength();

		// Energias iguais
		if(thisPlayerStrength == otherPlayerStrength){
			// Random 0 ou 1
			System.err.println(this.player.toString() + " HAS THE SAME STRENGTH OF " + opponent.toString());
			int chance = (int) Math.round( Math.random());
			switch (chance) {
				case 0 -> {
					this.player.addStrength(otherPlayerStrength);
					System.err.println(this.player + " PLAYER WON!"); //TODO Debug
					opponent.getCurrentCell().setObstacle(true);
					opponent.interrupt();
					return this.player;
				}
				case 1 -> {
					opponent.addStrength(thisPlayerStrength);
					System.err.println(opponent + " OPPONENT WON!"); //TODO Debug
					this.setObstacle(true);
					this.player.interrupt();
					return opponent;
				}
			}

		// O player nesta celula ganha ao outro que entra nesta celula
		} else if (thisPlayerStrength > otherPlayerStrength) {
			this.player.addStrength(opponent.getCurrentStrength());
			System.err.println(this.player + " PLAYER WON!"); //TODO Debug
			opponent.getCurrentCell().setObstacle(true);
			opponent.interrupt();
			return this.player;

		// O outro player ganha ao player que esta nesta celula
		} else {
			opponent.addStrength(this.player.getCurrentStrength());
			System.err.println(opponent + " OPPONENT WON!"); //TODO Debug
			this.setObstacle(true);
			this.player.interrupt();
			return opponent;
		}
		//Caso houve problema
		return null;

	}


	// Should not be used like this in the initial state: cell might be occupied, must coordinate this operation
	//TODO Deve ser so usado na inicializacao
	public void setPlayer(Player player) throws InterruptedException{
		lock.lock();
		try{
			while(this.player != null){
				System.out.println("Player " + player.getIdentification() + " TRIED TO SPAWN [!!OCCUPIED!!] AT " + getPosition()
						+ " || Player " + this.player.getIdentification() + " ALREADY THERE!!");
				isFull.await();
			}
			this.player = player;
		}finally {
			lock.unlock();
		}
	}

	/**
	 * Remove um player da celula
	 */
	public void removePlayer(){
		lock.lock();
		try{
			this.player = null;
			isFull.signal();
		}finally {
			lock.unlock();
		}
	}

	@Override
	public String toString(){
		return this.position.toString();
	}


	@Override
	public int compareTo(Cell o) {
		return this.id - o.getId();
	}
}
