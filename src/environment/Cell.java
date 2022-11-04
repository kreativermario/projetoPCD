package environment;

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

	private Player player=null;
	
	public Cell(Coordinate position,Game g) {
		super();
		this.position = position;
		this.game=g;
	}

	public Coordinate getPosition() {
		return position;
	}

	public boolean isOcupied() {
		return player!=null;
	}


	public Player getPlayer() {
		return player;
	}

	// Should not be used like this in the initial state: cell might be occupied, must coordinate this operation
	//TODO Sincronização
	public void setPlayer(Player player) throws InterruptedException{
		lock.lock();
		try{
			while(this.player != null){
				System.err.println("HÁ PLAYER NA MESMA LOCALIZAÇÃO!");
				isFull.await();
			}
			this.player = player;
		}finally {
			lock.unlock();
		}
	}

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
