package gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import game.Game;
import game.RemoteClient;
import game.Server;

import javax.swing.JFrame;

public class GameGuiMain implements Observer {
	private JFrame frame;
	private RemoteClient clientConnection;
	private BoardJComponent boardGui;
	private Game game;

	public GameGuiMain() {
		super();
		frame = new JFrame("pcd.io");
		game = new Game();
		game.addObserver(this);
		buildGui();
	}

	public GameGuiMain(RemoteClient remoteClient, Game game, BoardJComponent boardJComponent, int LEFT, int RIGHT, int UP, int DOWN) {
		super();
		this.clientConnection = remoteClient;
		this.game = game;
		frame = new JFrame("Cliente");
		game.addObserver(this);
		buildGui(boardJComponent);
	}

	private void buildGui(BoardJComponent boardJComponent) {
		boardGui = boardJComponent;
		frame.add(boardGui);
		frame.setSize(800,800);
		frame.setLocation(0, 150);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e) {
				clientConnection.closeConnection();
				super.windowClosing(e);
				// Do your disconnect from the DB here.
			}
		});
	}

	public void updateGameStatus(Game game){
		this.game = game;
		game.addObserver(this);
		boardGui.repaint();
	}

	private void buildGui() {
		boardGui = new BoardJComponent(game);
		frame.add(boardGui);


		frame.setSize(800,800);
		frame.setLocation(0, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init()  {
		frame.setVisible(true);

		// Demo players, should be deleted
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}

}
