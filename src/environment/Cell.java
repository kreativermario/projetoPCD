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

	public void setObstacle(boolean isObstacle){
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
			System.err.println(opponent.toString() + " is going to fight " + this.player.toString()); //TODO Debug
			mutex.acquire();
			//TODO tenho que freezar o outro player de mexer!
			int thisPlayerStrength = this.player.getCurrentStrength();
			int otherPlayerStrength = opponent.getCurrentStrength();

			if(thisPlayerStrength == otherPlayerStrength){
				System.err.println(opponent.toString() + " has SAME strength as " + this.player.toString()); //TODO Debug
				// Random 0 ou 1
				int chance = (int) Math.round( Math.random());
				switch(chance){
					case 0:
						this.player.addStrength(otherPlayerStrength);
						System.err.println(this.player + " WON!"); //TODO Debug
						opponent.getCurrentCell().setObstacle(true);
						opponent.interrupt();
						return;
					case 1:
						opponent.addStrength(thisPlayerStrength);
						System.err.println(opponent + " WON!"); //TODO Debug
						this.setObstacle(true);
						player.interrupt();
						return;
				}

			// O player nesta celula ganha ao outro que entra nesta celula
			} else if(thisPlayerStrength > otherPlayerStrength){
				this.player.addStrength(opponent.getCurrentStrength());
				System.err.println(this.player + " WON!"); //TODO Debug
				opponent.getCurrentCell().setObstacle(true);
				opponent.interrupt();
				return;

			// O outro player ganha ao player que esta nesta celula
			}else{
				opponent.addStrength(this.player.getCurrentStrength());
				System.err.println(opponent + " WON!"); //TODO Debug
				this.setObstacle(true);
				this.player.interrupt();
				return;
			}

		}finally {
			mutex.release();
			System.err.println(this.getPosition() + " RELEASING MUTEX: " + mutex.toString()); //TODO Debug
		}

	}


	// Should not be used like this in the initial state: cell might be occupied, must coordinate this operation
	//TODO Deve ser so usado na inicializacao
	public void setPlayer(Player player) throws InterruptedException{
		lock.lock();
		try{
			while(this.player != null){
				System.out.println(Thread.currentThread().toString() + " STUCK AT " + getPosition());
				isFull.await();
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
