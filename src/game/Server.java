
package game;

import environment.Direction;
import gui.BoardJComponent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{

    private ServerSocket server;
    protected  Game game;
    public static final int PORT = 2022;

    public Server() {
        System.err.println("Server created!");
        this.game = new Game();
        try{
            server = new ServerSocket(PORT); // Throws IOException
        }catch (IOException e){
            System.err.println("Error connecting server... aborting!");
            System.exit(1);
        }
    }

    //Server com GUI
    public Server(Game game) {
        System.err.println("Server created!");
        this.game = game;
        try{
            server = new ServerSocket(PORT); // Throws IOException
        }catch (IOException e){
            System.err.println("Error connecting server... aborting!");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        runServer();
    }

    public void runServer() {
        while (true){
            try {
                waitForConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForConnection() throws IOException{
        System.out.println("Waiting for connection...");
        Socket connection = server.accept();
        ConnectionHandler connectionHandler = new ConnectionHandler(connection);
        connectionHandler.start();
    }


    //Classe que trata das conexões dos vários clientes, é a thread que trata
    public class ConnectionHandler extends Thread{

        // O que é necessário para ligar um cliente
        private Socket connection;
        private BufferedReader input;
        private ObjectOutputStream output;

        public ConnectionHandler(Socket connection){
            this.connection = connection;
        }

        @Override
        public void run(){
            try {
                getStreams();
                proccessConnection();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }

        }

        public void getStreams() throws IOException{
            //TODO Autoflush, quando escrevo algo, manda logo
            output = new ObjectOutputStream(connection.getOutputStream());

            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        }

        public void proccessConnection() throws IOException, InterruptedException {
            Player player = new HumanPlayer(game);
            player.start();
            System.out.println("Successful connection, starting proccessing...");
            while(true){
                sleep(Game.REFRESH_INTERVAL);
                System.out.println("Sending board to client...");
                output.writeObject(player);
                System.out.println("Sent board to client!");
                output.flush();
                //output.println("Echo: " + message);
                String directionReceived = input.readLine();
                if(directionReceived != null){
                    System.out.println("DIRECTION RECEIVED !!! " + directionReceived);
                    player.setMoveDirection(Direction.valueOf(directionReceived));
                }

            }
        }

        public void closeConnection() {

             /*Este método não é o mais correto, porque se input der erro não vai fechar o resto tipo o output,
            RESOURCE LEAK! Teria que fazer try catch para cada um.*/
            try{
                input.close();
                output.close();
                connection.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }


}

