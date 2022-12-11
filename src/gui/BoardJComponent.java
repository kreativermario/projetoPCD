package gui;

import environment.Cell;
import environment.Coordinate;
import environment.Direction;
import game.Game;
import game.Player;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Creates a JComponent to display the game state.
 * At the same time, this is also a KeyListener for itself: when a key is pressed,
 * attribute lastPressedDirection is updated accordingly. This feature is a demo to
 * better understand how to deal with keys pressed, useful for the remote client.
 * This feature is not helpful for the main application and should be ignored.
 * This class does not need to be edited.
 * @author luismota
 *
 */
public class BoardJComponent extends JComponent implements KeyListener {
	private Game game;
	private List<Player> playerList;
	private Cell[][] board;
	private final Image obstacleImage = new ImageIcon("obstacle.png").getImage();
	private final Image humanPlayerImage= new ImageIcon("abstract-user-flat.png").getImage();
	private Direction lastPressedDirection=null;
	private final boolean customKeys;
	private int LEFT;
	private int RIGHT;
	private int UP;
	private int DOWN;


	public BoardJComponent(Game game) {
		this.game = game;
		this.customKeys = false;
		setFocusable(true);
		addKeyListener(this);
	}

	public BoardJComponent(List<Player> playerList, int LEFT, int RIGHT, int UP, int DOWN){
		this.playerList = playerList;
		this.customKeys = true;
		this.LEFT = LEFT;
		this.RIGHT = RIGHT;
		this.UP = UP;
		this.DOWN = DOWN;
		setFocusable(true);
		addKeyListener(this);
	}

	public BoardJComponent(Cell[][] board, int LEFT, int RIGHT, int UP, int DOWN){
		this.board = board;
		this.customKeys = true;
		this.LEFT = LEFT;
		this.RIGHT = RIGHT;
		this.UP = UP;
		this.DOWN = DOWN;
		setFocusable(true);
		addKeyListener(this);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double cellHeight=getHeight()/(double)Game.DIMY;
		double cellWidth=getWidth()/(double)Game.DIMX;

		for (int y = 1; y < Game.DIMY; y++) {
			g.drawLine(0, (int)(y * cellHeight), getWidth(), (int)(y* cellHeight));
		}
		for (int x = 1; x < Game.DIMX; x++) {
			g.drawLine( (int)(x * cellWidth),0, (int)(x* cellWidth), getHeight());
		}

		for (int x = 0; x < Game.DIMX; x++) {
			for (int y = 0; y < Game.DIMY; y++) {
				Player player = null;
				Coordinate p = new Coordinate(x, y);
				if(!customKeys){
					player = game.getCell(p).getPlayer();
				}else {
					for (Player ply : playerList) {
						if(ply.getCurrentCell().getPosition().equals(p))
							player = ply;
					}
				}

				if (player != null) {
					// Fill yellow if there is a dead player
					if (player.getCurrentStrength() == 0) {
						g.setColor(Color.YELLOW);
						g.fillRect((int) (p.x * cellWidth),
								(int) (p.y * cellHeight),
								(int) (cellWidth), (int) (cellHeight));
						g.drawImage(obstacleImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight),
								(int) (cellWidth), (int) (cellHeight), null);
						// if player is dead, don'd draw anything else?
						continue;
					}
					// Fill green if it is a human player
					if (player.isHumanPlayer()) {
						g.setColor(Color.GREEN);
						g.fillRect((int) (p.x * cellWidth),
								(int) (p.y * cellHeight),
								(int) (cellWidth), (int) (cellHeight));
						// Custom icon?
						g.drawImage(humanPlayerImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight),
								(int) (cellWidth), (int) (cellHeight), null);
					}
					g.setColor(new Color(player.getIdentification() * 1000));
					((Graphics2D) g).setStroke(new BasicStroke(5));
					Font font = g.getFont().deriveFont((float) cellHeight);
					g.setFont(font);
					String strengthMarking = (player.getCurrentStrength() == 10 ? "X" : "" + player.getCurrentStrength());
					g.drawString(strengthMarking,
							(int) ((p.x + .2) * cellWidth),
							(int) ((p.y + .9) * cellHeight));
				}

			}
		}
	}



	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}


	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(!customKeys){
			switch (keyCode) {
				case KeyEvent.VK_LEFT -> {
					System.out.println("PRESSED LEFT");
					lastPressedDirection = Direction.LEFT;
				}
				case KeyEvent.VK_RIGHT -> {
					System.out.println("PRESSED RIGHT");
					lastPressedDirection = Direction.RIGHT;
				}
				case KeyEvent.VK_UP -> {
					System.out.println("PRESSED UP");
					lastPressedDirection = Direction.UP;
				}
				case KeyEvent.VK_DOWN -> {
					System.out.println("PRESSED DOWN");
					lastPressedDirection = Direction.DOWN;
				}
			}
		}else{
			if(keyCode == UP){
				System.out.println("PRESSED UP");
				lastPressedDirection=environment.Direction.UP;
				return;
			}
			if(keyCode == DOWN){
				System.out.println("PRESSED DOWN");
				lastPressedDirection=environment.Direction.DOWN;
				return;
			}
			if(keyCode == RIGHT){
				System.out.println("PRESSED RIGHT");
				lastPressedDirection=environment.Direction.RIGHT;
				return;
			}
			if(keyCode == LEFT) {
				System.out.println("PRESSED LEFT");
				lastPressedDirection=environment.Direction.LEFT;
			}
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		//ignore
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Ignored...
	}

	public Direction getLastPressedDirection() {
		return lastPressedDirection;
	}

	public void clearLastPressedDirection() {
		lastPressedDirection=null;
	}
}
