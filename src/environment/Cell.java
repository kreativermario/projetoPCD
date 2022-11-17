package environment;

import coordination.CellSemaphore;
import game.Game;
import game.Player;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Cell {
	private Coordinate position;
	private Game game;
	private Lock lock = new ReentrantLock();
	private Condition isFull = lock.newCondition();
	private CellSemaphore mutex = new CellSemaphore(1);
	private Player player=null;
	private boolean isObstacle;

	public Cell(Coordinate position,Game g) {
		super();
		this.position = position;
		this.game=g;
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


	/**
	 * Metodo que trata a disputa entre dois players
	 * @param opponent
	 */
	public void fight(Player opponent) throws InterruptedException{
		try{
			mutex.acquire(); //TODO Debug
			System.err.println(" ACQUIRING MUTEX -->> " + this.getPosition() + " " + mutex.toString()); //TODO Debug
			System.err.println(this.player.toString() + " is going to fight " + opponent.toString()); //TODO Debug
			//TODO tenho que freezar o outro player de mexer!
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
					}
					case 1 -> {
						opponent.addStrength(thisPlayerStrength);
						System.err.println(opponent + " OPPONENT WON!"); //TODO Debug
						this.setObstacle(true);
						this.player.interrupt();
					}
				}

			// O player nesta celula ganha ao outro que entra nesta celula
			} else if (thisPlayerStrength > otherPlayerStrength) {
				this.player.addStrength(opponent.getCurrentStrength());
				System.err.println(this.player + " PLAYER WON!"); //TODO Debug
				opponent.getCurrentCell().setObstacle(true);
				opponent.interrupt();

			// O outro player ganha ao player que esta nesta celula
			} else {
				opponent.addStrength(this.player.getCurrentStrength());
				System.err.println(opponent + " OPPONENT WON!"); //TODO Debug
				this.setObstacle(true);
				this.player.interrupt();
			}

		} finally {
			mutex.release();
			System.err.println(" RELEASING MUTEX -->> " + this.getPosition() + " " + mutex.toString()); //TODO Debug
		}

	}


	// Should not be used like this in the initial state: cell might be occupied, must coordinate this operation
	//TODO Deve ser so usado na inicializacao
	public void setPlayer(Player player, boolean isMove) throws InterruptedException{
		lock.lock();
		try{
			while(this.player != null){
				if(!isMove) {
					System.out.println("Player " + player.getIdentification() + " TRIED TO SPAWN [!!OCCUPIED!!] AT " + getPosition()
							+ " || Player " + this.player.getIdentification() + " ALREADY THERE!!");
					isFull.await();
				}
			}
			this.player = player;
		}finally {
			lock.unlock();
		}
	}

	/**
	 * Remove um player da celula
	 * @throws InterruptedException
	 */
	public void removePlayer() throws InterruptedException{
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


}
