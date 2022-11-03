package game;


import environment.Cell;
import environment.Coordinate;

import java.util.Observable;
import java.util.Random;

public class Game extends Observable {

	public static final int DIMY = 5;
	public static final int DIMX = 5;
	private static final int NUM_PLAYERS = 4; //TODO era 90 players
	private static final int NUM_FINISHED_PLAYERS_TO_END_GAME=3;

	public static final long REFRESH_INTERVAL = 400;
	public static final double MAX_INITIAL_STRENGTH = 3;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;

	protected Cell[][] board;

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
	
		for (int x = 0; x < Game.DIMX; x++) 
			for (int y = 0; y < Game.DIMY; y++) 
				board[x][y] = new Cell(new Coordinate(x, y),this);
		addPlayersToGame();
	}

	/**
	 * Gera jogadores bots para o jogo
	 */
	private void addPlayersToGame(){
		for(int i = 1; i <= Game.NUM_PLAYERS; i++){
			Random r = new Random();
			int low = 1;
			int high = 4; // Exclusivo, logo é de 1 a 3 random
			int strength = r.nextInt(high-low) + low;

			Player player = new Player(i,this, (byte) strength) {
				@Override
				public boolean isHumanPlayer() {
					return false;
				}
			};

			Coordinate initCoordinate = Coordinate.getRandomCoordinate();
			//TODO Talvez livrar de um duplo for so para pesquisar a celula e colocar player. Fazer um get diretamente
			//TODO atraves da geracao de uma coordenada aleatoria e colocar la

			//TODO se a celula ja tiver player, fazer aqui um wait e depois colocar. Precisamos de implementar movimento
			// E o clock do jogo
			if(board[initCoordinate.x][initCoordinate.y].isOcupied()){
				System.err.println("Célula " + board[initCoordinate.x][initCoordinate.y].getPosition()
				+ " já tem player");
			}else{
				board[initCoordinate.x][initCoordinate.y].setPlayer(player);
			}

			/*for (int x = 0; x < Game.DIMX; x++) {
				for (int y = 0; y < Game.DIMY; y++) {
					if (board[x][y].getPosition().equals(initialPos.getPosition())) {
						board[x][y].setPlayer(player);
					}
				}
			}*/

			System.err.println("Player generated -> " + player.toString());
			// To update GUI
			notifyChange();

		}

	}

	/**
	 * Método para obter a Cell por um Player dado
	 * @param player
	 * @return Cell
	 */
	public Cell getCellByPlayer(Player player){
		for (int x = 0; x < Game.DIMX; x++)
			for (int y = 0; y < Game.DIMY; y++)
				if(board[x][y].getPlayer() != null)
					if (board[x][y].getPlayer().equals(player)) return board[x][y];
		return null; // Return null se nao for encontrado
	}



	/** 
	 * @param player 
	 */
	public void addPlayerToGame(Player player) {
		Cell initialPos=getRandomCell();
		initialPos.setPlayer(player);
		
		// To update GUI
		notifyChange();
		
	}

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
