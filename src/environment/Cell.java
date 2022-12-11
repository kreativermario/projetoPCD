package environment;

import coordination.AutonomousThread;
import game.Game;
import game.Player;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe da Cell que contem o Player
 */
public class Cell implements Comparable<Cell>, Serializable{
	public Lock lock = new ReentrantLock();
	public Lock moveLock = new ReentrantLock();
	private Condition isFull = lock.newCondition();
	private Player player=null;
	private boolean isObstacle = false;
	private final Coordinate position;
	private final Game game;
	private final int id;


	// Variaveis estaticas
	private static AtomicInteger idCounter = new AtomicInteger();

	//TODO Variavel para debug que identifica os confrontos
	private static AtomicInteger fightCounter = new AtomicInteger();

	/**
	 * Metodo construtor
	 * @param position Coordinate
	 * @param g Game
	 */
	public Cell(Coordinate position,Game g) {
		super();
		id = createID();
		this.position = position;
		this.game=g;
	}

	/**
	 * Método que retorna a identificacao unica da Cell
	 * @return id int
	 */
	private int getId(){
		return id;
	}

	/**
	 * Metodo que cria a identificacao unica da Cell
	 * @return id int
	 */
	private int createID() {
		return idCounter.getAndIncrement();
	}

	/**
	 * Metodo que cria um id unico para cada encontro para DEBUG
	 * @return fightId int
	 */
	private int createFightID() {
		return fightCounter.getAndIncrement();
	}

	/**
	 * Metodo principal utilizado pelos players para mover
	 * @param to Cell
	 * @throws InterruptedException
	 */
	public void transferPlayer(Cell to) throws InterruptedException {

		int fightId = createFightID();
		//System.err.println("Encounter" + fightId + " || TRANSFER BETWEEN " + this + " AND " + to);

		//TODO try locks
		if(this.compareTo(to) > 0){
			this.moveLock.lock();
			//System.err.println("Encounter" + fightId + " || locked " + this);
			to.moveLock.lock();
			//System.err.println("Encounter" +  fightId+ " || locked " + to);
		}else{
			to.moveLock.lock();
			//System.err.println("Encounter" +  fightId + " || locked " + to);
			this.moveLock.lock();
			//System.err.println("Encounter" + fightId + " || locked " + this);
		}

		//TODO locks com sucesso vou fazer disputa
		Player fromPlayer = this.getPlayer();

		if(to.isOcupied()){
			//TODO thread player automatico fica à espera que alguem o desbloqueie!
			while(to.isObstacle && !fromPlayer.isHumanPlayer()){
				synchronized (this){
					try {
						new AutonomousThread(Thread.currentThread()).start();
						wait();
					} catch (InterruptedException e) {
						if(this.compareTo(to) > 0){
							this.moveLock.unlock();
							//System.err.println("Encounter" + fightId + " || Unlocked " + this);
							to.moveLock.unlock();
							//System.err.println("Encounter" + fightId + " || Unlocked " + to);
						}else{
							to.moveLock.unlock();
							//System.err.println("Encounter" + fightId + " || Unlocked " + to);
							this.moveLock.unlock();
							//System.err.println("Encounter" + fightId + " || Unlocked " + this);
						}
						//Caso o jogo acabou, nao quero que o player tente mover outra vez, delegar a excecao e interrupt
						if(game.isGameEnded()) throw new InterruptedException();
						return;
					}
				}
			}

			if(!to.isObstacle() && to.getPlayer().getCurrentStrength()!= 10){
				System.err.println("Encounter" + fightId + " || " + this.player + " is going to fight " + to.getPlayer()); //TODO Debug
				to.fight(fromPlayer);
			}
		// Não tem player, então é só mover!
		}else {
			this.removePlayer();
			to.setPlayer(fromPlayer);
		}

		//TODO Acabou a disputa, dar unlock
		if(this.compareTo(to) > 0){
			this.moveLock.unlock();
			//System.err.println("Encounter" + fightId + " || Unlocked " + this);
			to.moveLock.unlock();
			//System.err.println("Encounter" + fightId + " || Unlocked " + to);
		}else{
			to.moveLock.unlock();
			//System.err.println("Encounter" + fightId + " || Unlocked " + to);
			this.moveLock.unlock();
			//System.err.println("Encounter" + fightId + " || Unlocked " + this);
		}

	}


	/**
	 * Metodo que trata a disputa entre dois players
	 * @param opponent Player
	 */
	private void fight(Player opponent){

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
					opponent.setDead();
					opponent.getCurrentCell().setObstacle();
					opponent.interrupt();
				}
				case 1 -> {
					opponent.addStrength(thisPlayerStrength);
					System.err.println(opponent + " OPPONENT WON!"); //TODO Debug
					this.player.setDead();
					this.setObstacle();
					this.player.interrupt();
				}
			}

		// O player nesta celula ganha ao outro que entra nesta celula
		} else if (thisPlayerStrength > otherPlayerStrength) {
			this.player.addStrength(opponent.getCurrentStrength());
			System.err.println(this.player + " PLAYER WON!"); //TODO Debug
			opponent.setDead();
			opponent.getCurrentCell().setObstacle();
			opponent.interrupt();

			// O outro player ganha ao player que esta nesta celula
		} else {
			opponent.addStrength(this.player.getCurrentStrength());
			System.err.println(opponent + " OPPONENT WON!"); //TODO Debug
			this.player.setDead();
			this.setObstacle();
			this.player.interrupt();
		}
		//Caso houve problema

	}


	/**
	 * Metodo utilizado para colocar player na cell
	 * @param player Player
	 * @throws InterruptedException
	 */
	public void setPlayer(Player player) throws InterruptedException{
		lock.lock();
		try{
			while(this.player != null){
				// Se a celula gerada for um player morto, tentar fazer spawn outra vez
				while(isObstacle()) {
					synchronized (this) {
						System.err.println(player + " TENTOU SPAWNAR EM CIMA DE UM DEAD PLAYER");
						wait(Game.MAX_WAITING_TIME_FOR_MOVE);
						game.addPlayerToGame(player);
					}
				}
				System.out.println("Player " + player.getIdentification() + " TRIED TO SPAWN [!!OCCUPIED!!] AT "
						+ getPosition()
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

	/**
	 * Obter a coordenada da celula
	 * @return position
	 */
	public Coordinate getPosition() {
		return position;
	}


	/**
	 * Devolve o player que esta atualmente na celula
	 * @return player
	 */
	public Player getPlayer() {
		lock.lock();
		Player returnPlayer = player;
		lock.unlock();
		return returnPlayer;
	}

	/**
	 * Devolve se a celula esta ocupada por um player
	 * @return boolean
	 */
	public boolean isOcupied() {
		return getPlayer()!=null;
	}

	/**
	 * Funcao que coloca a celula como obstaculo
	 */
	private void setObstacle(){
		this.isObstacle = true;
	}

	/**
	 * Devolve se o player que esta na cell e um obstaculo ou nao
	 * @return result boolean
	 */
	public boolean isObstacle(){
		lock.lock();
		boolean result;
		try{
			result= isObstacle;
		}finally {
			lock.unlock();
		}
		return result;
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
