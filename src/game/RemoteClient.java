package game;

import gui.BoardJComponent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;


public class RemoteClient implements Observer {
    private JFrame frame = new JFrame("client");
    private InetAddress address;
    private BoardJComponent boardGui;
    private Socket socket;
    private ObjectInputStream input;
    private PrintWriter output;
    private final int PORT;
    //TODO nao preciso de game
    private Game game;
    private int LEFT;
    private int RIGHT;
    private int UP;
    private int DOWN;

    //Construtor para GUI Clientes
    public RemoteClient(InetAddress address, int PORT, int LEFT, int RIGHT, int UP, int DOWN) {
        super();
        //TODO nao preciso do game
        //this.game = new Game();
        this.address = address;
        this.PORT = PORT;
        //TODO receber o BOARDJCOMPONENT DO SERVER
        runClient();

    }

    private void buildGui() {
        frame.add(boardGui);

        frame.setSize(800,800);
        frame.setLocation(0, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void runClient(){
        try {
            connectToServer();
            getStreams();
            proccessConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeConnection();
        }

    }

    public void connectToServer(){
        try {
            socket = new Socket(address, PORT);
            System.err.println("Client " + address + " CONNECTED TO SERVER!");
        } catch (IOException e) {
            System.err.println("Client " + address + " error connecting... exiting");
            System.exit(1);
        }
    }

    public void getStreams() throws IOException{
        //TODO Autoflush, quando escrevo algo, manda logo
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new ObjectInputStream(socket.getInputStream());

    }

    public void proccessConnection() throws IOException {
        System.out.println("Client processing continuous connection...");
        int firstConnection = 0;
        while(true){
            try {
                game = (Game) input.readObject();
                if(firstConnection == 0){
                    firstConnection++;
                    boardGui = new BoardJComponent(game, LEFT, RIGHT, UP, DOWN);
                    buildGui();
                }
                boardGui.setGame(game);
                game.addObserver(this);
                boardGui.repaint();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void closeConnection(){

        try{
            input.close();
            output.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        boardGui.repaint();
    }

    public static void main(String[] args) throws UnknownHostException {
        RemoteClient client = new RemoteClient(InetAddress.getByName("localHost"), 2022,
                37, 39, 38, 40);
        System.out.println(InetAddress.getByName("localHost"));
    }

}


