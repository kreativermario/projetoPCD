package gui;

import environment.Cell;
import game.Player;

import javax.swing.*;
import java.util.List;

public class ClientGUI {
    private final JFrame frame;
    private final BoardJComponent boardGui;

    public ClientGUI(List<Player> playerList, int LEFT, int RIGHT, int UP, int DOWN) {
        super();
        frame = new JFrame("Cliente");
        boardGui = new BoardJComponent(playerList, LEFT, RIGHT, UP, DOWN);
        buildGui();
    }

    public ClientGUI(Cell[][] gameBoard, int LEFT, int RIGHT, int UP, int DOWN) {
        super();
        frame = new JFrame("Cliente");
        boardGui = new BoardJComponent(gameBoard, LEFT, RIGHT, UP, DOWN);
        buildGui();
    }

    private void buildGui() {
        frame.add(boardGui);
        frame.setSize(800,800);
        frame.setLocation(0, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void updateGameStatus(List<Player> playerList){
        boardGui.setPlayerList(playerList);
        boardGui.repaint();
    }


    public BoardJComponent getBoardJComponent() {
        return boardGui;
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


}
