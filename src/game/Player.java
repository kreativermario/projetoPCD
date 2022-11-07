package game;



import environment.Cell;

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
		}catch (InterruptedException e){
			e.printStackTrace();
		}

	}

	/**
	 * Torna o player num obstaculo se perdeu
	 */
	public void setObstacle(){
		this.currentStrength = 0;
	}


	/**
	 * Metodo utilizado para adicionar energia ao jogador depois de uma disputa
	 * @param strength
	 */
	public void addStrength(int strength){
		if(getCurrentStrength() + strength >= 10){
			//TODO Finish game
			System.out.println("Player " + getIdentification()  + " -> Finished the game!");
		}
		this.currentStrength += strength;
	}
	
	
	

	public abstract void move() throws InterruptedException;
	public abstract void run();
	public abstract boolean isHumanPlayer();
	
	@Override
	public String toString() {
		return "Player [id=" + id + ", currentStrength=" + currentStrength + ", getCurrentCell()=" + getCurrentCell()
		+ "]";
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
