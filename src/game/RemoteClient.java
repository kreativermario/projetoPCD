package game;

import environment.Cell;
import environment.Direction;
import gui.BoardJComponent;
import gui.GameGuiMain;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;


public class RemoteClient{
    private InetAddress address;
    private Socket socket;
    private ObjectInputStream input;
    private PrintWriter output;
    private final int PORT;
    //TODO nao preciso de game
    private Game game;
    private GameGuiMain clientGUI;
    private Player clientPlayer;
    private BoardJComponent boardJComponent;
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
        this.LEFT = LEFT;
        this.RIGHT = RIGHT;
        this.UP = UP;
        this.DOWN = DOWN;
        //TODO receber estado jogo do server
        //TODO iniciar GUI

    }

    public void runClient() {
        try {
            connectToServer();
            getStreams();
            firstConnection();
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
        output = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())),
                true);
        //output = new PrintWriter(socket.getOutputStream(), true);
        input = new ObjectInputStream(socket.getInputStream());

    }

    public void firstConnection() throws IOException{
        System.out.println("Client processing first connection...");
        while(true){
            try {
                //TODO receção
                //Game receivedGame = (Game) input.readObject();
                Player receivedPlayer = (Player) input.readObject();
                if(receivedPlayer != null){
                    clientPlayer = receivedPlayer;
                    game = clientPlayer.getGame();
                    boardJComponent = new BoardJComponent(game, LEFT, RIGHT, UP, DOWN);
                    clientGUI = new GameGuiMain(game, boardJComponent,LEFT, RIGHT, UP, DOWN);
                    clientGUI.init();
                    break;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void proccessConnection() throws IOException {
        System.out.println("Client processing continuous connection...");
        Direction directionPressed;
        while(true){
            try {
                //TODO receção
                //Game receivedGame = (Game) input.readObject();
                Player receivedPlayer = (Player) input.readObject();
                System.out.println(receivedPlayer.toString());
                if(receivedPlayer != null){
                    System.out.println("UPDATING STATUS...");
                    clientGUI.updateGameStatus(game);
                    //TODO envio de direcao
                    if(boardJComponent.getLastPressedDirection() != null) {
                        directionPressed = boardJComponent.getLastPressedDirection();
                        boardJComponent.clearLastPressedDirection();
                        System.out.println("SENDING " + directionPressed.toString());
                        output.println(directionPressed.toString());
                    }
                }
//                Cell[][] receivedBoard = receivedGame.getBoard();
//                game.updateBoard(receivedBoard);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void closeConnection(){
        try{
            System.out.println("Closing connection...");
            input.close();
            output.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        RemoteClient client = new RemoteClient(InetAddress.getByName("localHost"), 2022,
                37, 39, 38, 40);
        client.runClient();
        System.out.println(InetAddress.getByName("localHost"));
    }

}


