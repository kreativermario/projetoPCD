package game;


import environment.Cell;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class Game extends Observable {

	public static final int DIMY = 15;
	public static final int DIMX = 15;
	private static final int NUM_PLAYERS = 4; //TODO era 90 players

	private static final int NUM_FINISHED_PLAYERS_TO_END_GAME=3;

	public static final long REFRESH_INTERVAL = 400;
	public static final double MAX_INITIAL_STRENGTH = 3;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;
	protected Cell[][] board;

	//Lista de bots
	private List<Cell> bots = new ArrayList<Cell>();


	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
	
		for (int x = 0; x < Game.DIMX; x++) 
			for (int y = 0; y < Game.DIMY; y++) 
				board[x][y] = new Cell(new Coordinate(x, y),this);
		addPlayersToGame(); // Adiciona jogadores reais

		for(Cell cell : bots){
			System.err.println(cell.getPlayer());
		}
	}

	/**
	 * Colocar jogadores no jogo
	 */
	private void addPlayersToGame(){
		for(int i = 1; i <= NUM_PLAYERS; i++){
			Random r = new Random();
			int low = 1;
			int high = (int) MAX_INITIAL_STRENGTH + 1; // Exclusivo, logo é de 1 a 3 random
			int strength = r.nextInt(high-low) + low;
			Player player = new BotPlayer(i, this, (byte) strength);

			Coordinate initCoordinate = Coordinate.getRandomCoordinate();
			//TODO Teste Coordinate initCoordinate = new Coordinate(4,4);

			try{
				Cell initCell = getCell(initCoordinate);
				initCell.setPlayer(player);
				bots.add(initCell);
				notifyChange();
			}catch (InterruptedException e){
				System.err.println("Player ja esta na mesma localizacao");
			}

		}

	}

	/**
	 * Método para obter a Cell por um Player dado
	 * @param player
	 * @return Cell
	 */
	public Cell getCellByPlayer(Player player){
		for(Cell cell : bots){
			if (cell.getPlayer().equals(player)){
				return cell;
			}
		}
		return null;
	}



	/** 
	 * @param player 
	 */
//	public void addPlayerToGame(Player player) {
//		Cell initialPos=getRandomCell();
//		initialPos.setPlayer(player);
//
//		// To update GUI
//		notifyChange();
//
//	}

	/**
	 * Obtém a célula dado uma coordenada
	 * @param at Coordinate
	 * @return board[at.x][at.y] Cell
	 */
	public Cell getCell(Coordinate at) {
		return board[at.x][at.y];
	}

	/**	
	 * Updates GUI. Should be called anytime the game state changes
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

	public Cell getRandomCell() {
		Cell newCell=getCell(new Coordinate((int)(Math.random()*Game.DIMX),(int)(Math.random()*Game.DIMY)));
		return newCell; 
	}
}
