package game;

import environment.Direction;
import gui.ClientGUI;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import static java.lang.Thread.sleep;


public class RemoteClient{
    private InetAddress address;
    private Socket socket;
    private ObjectInputStream input;
    private PrintWriter output;
    private final int PORT;
    //TODO nao preciso de game
    private Game game;
    private ClientGUI clientGUI;
    private Player clientPlayer;
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
                GameStatus receivedGameStatus = (GameStatus) input.readObject();
                StringBuilder debug = new StringBuilder(receivedGameStatus.toString());
                for(Player p : receivedGameStatus.getPlayerList()){
                    debug.append(" ").append(p.getCurrentCell());
                }
                System.out.println(debug);

                List<Player> playerList = receivedGameStatus.getPlayerList();
                clientGUI = new ClientGUI(playerList, LEFT, RIGHT, UP, DOWN);
                clientGUI.init();
                break;
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
                GameStatus receivedGameStatus = (GameStatus) input.readObject();
                System.out.println("UPDATING STATUS...");
                StringBuilder debug = new StringBuilder(receivedGameStatus.toString());
                for(Player p : receivedGameStatus.getPlayerList()){
                    debug.append(" ").append(p.getCurrentCell());
                }
                System.out.println(debug);
                clientGUI.updateGameStatus(receivedGameStatus.getPlayerList());
                //TODO envio de direcao

                if(clientGUI.getBoardJComponent().getLastPressedDirection() != null) {
                    directionPressed = clientGUI.getBoardJComponent().getLastPressedDirection();
                    clientGUI.getBoardJComponent().clearLastPressedDirection();
                    System.out.println("SENDING " + directionPressed.toString());
                    output.println(directionPressed.toString());
                }

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

}


