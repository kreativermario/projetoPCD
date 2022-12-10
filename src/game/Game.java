package game;


import coordination.FinishCountDownLatch;
import environment.Cell;
import environment.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Classe principal que processa o jogo
 */
public class Game extends Observable implements Serializable {

	// Variaveis estaticas
	public static final int DIMY = 15;
	public static final int DIMX = 15;
	private static final int NUM_PLAYERS = 60; //TODO era 90 players
	private static final int NUM_FINISHED_PLAYERS_TO_END_GAME=3;
	public static final long REFRESH_INTERVAL = 400;
	public static final double MAX_INITIAL_STRENGTH = 3;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;
	public static FinishCountDownLatch countDownLatch = new FinishCountDownLatch(NUM_FINISHED_PLAYERS_TO_END_GAME);

	private boolean gameEnded = false;
	protected Cell[][] board;

	/**
	 * Metodo construtor do jogo
	 */
	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];

		for (int x = 0; x < Game.DIMX; x++)
			for (int y = 0; y < Game.DIMY; y++)
				board[x][y] = new Cell(new Coordinate(x, y),this);
		// Adiciona bots ao jogo
		addBotsToGame();

		// Inicia a thread que espera que o jogo acabe e interrompe todos os players
		Thread endGame =  new Thread() {
			@Override
			public void run(){
				try {
					countDownLatch.await();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				//TODO matar threads e terminar jogo
				for (int x = 0; x < Game.DIMX; x++) {
					for (int y = 0; y < Game.DIMY; y++) {
						Cell cell = board[x][y];
						//TODO ter atencao sincronizacao etc! Dar lock!
						if(cell.isOcupied() && !cell.isObstacle()){
							cell.getPlayer().interrupt();
						}
					}
				}
				gameEnded = true;
				//TODO O que fazer quando acabar
				System.err.println("GAME FINISHED!");
			}
		};
		endGame.start();
		//TODO Debug morte
		Server server = new Server(this);
		server.start();

	}


	/**
	 * Colocar jogadores automaticos no jogo
	 */
	private void addBotsToGame(){
		for(int i = 1; i <= NUM_PLAYERS; i++){
			Random r = new Random();
			int low = 1;
			int high = (int) MAX_INITIAL_STRENGTH + 1; // Exclusivo, logo é de 1 a 3 random
			int strength = r.nextInt(high-low) + low;
			Player player = new BotPlayer(i, this, (byte) strength);
			player.start();
		}
	}

	/**
	 *
	 * @param player
	 * @throws InterruptedException
	 */
	public void addPlayerToGame(Player player) throws InterruptedException{
		Coordinate initCoordinate = Coordinate.getRandomCoordinate();
		Cell initCell = getCell(initCoordinate);
		initCell.setPlayer(player);
	}


	public boolean isGameEnded() {
		return gameEnded;
	}

	/**
	 * Método para obter a Cell por um Player dado
	 * @param player
	 * @return Cell
	 */
	public Cell getCellByPlayer(Player player){
		for (int x = 0; x < Game.DIMX; x++) {
			for (int y = 0; y < Game.DIMY; y++) {
				Player ply = board[x][y].getPlayer();
				if (ply != null) {
					if (ply.equals(player))
						return board[x][y];
				}
			}
		}
		return null;
	}

	/**
	 * Obtém a célula dado uma coordenada
	 * @param at Coordinate
	 * @return board[at.x][at.y] Cell
	 */
	public Cell getCell(Coordinate at) {
		return board[at.x][at.y];
	}

	public Cell[][] getBoard() {
		return board;
	}
	/**
	 * Updates GUI. Should be called anytime the game state changes
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

	public Cell getRandomCell() {
		return getCell(new Coordinate((int)(Math.random()*Game.DIMX),(int)(Math.random()*Game.DIMY)));
	}
}
