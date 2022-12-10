
package game;

import environment.Direction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{

    private ServerSocket server;
    protected Game game;
    public static final int PORT = 2022;

    /**
     * Metodo construtor utilizado para Server sem GUI
     */
    public Server() {
        System.out.println("Server created!");
        this.game = new Game();
        try{
            server = new ServerSocket(PORT); // Throws IOException
        }catch (IOException e){
            System.out.println("Error connecting server... aborting!");
            System.exit(1);
        }
    }

    /**
     * Metodo construtor para Server com GUI, o Game cria o Server
     * @param game Game
     */
    public Server(Game game) {
        System.out.println("Server created!");
        this.game = game;
        try{
            server = new ServerSocket(PORT); // Throws IOException
        }catch (IOException e){
            System.out.println("Error connecting server... aborting!");
            System.exit(1);
        }
    }

    /**
     * Server tem de ser thread porque e criado pelo Game se queremos GUI
     */
    @Override
    public void run(){
        while(true){
            runServer();
        }
    }

    /**
     * Metodo principal do servidor que espera por conexoes
     */
    public void runServer() {
        while (true){
            try {
                waitForConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cria um handler por conexao
     * @throws IOException
     */
    public void waitForConnection() throws IOException{
        System.out.println("Waiting for connection...");
        Socket connection = server.accept();
        ConnectionHandler connectionHandler = new ConnectionHandler(connection);
        connectionHandler.start();
    }


    //Classe que trata das conexões dos vários clientes, é a thread que trata
    public class ConnectionHandler extends Thread{

        // O que é necessário para ligar um cliente
        private final Socket connection;
        private BufferedReader input;
        private ObjectOutputStream output;

        public ConnectionHandler(Socket connection){
            this.connection = connection;
        }

        @Override
        public void run(){
            try {
                getStreams();
                proccessStreams();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }

        }

        private void getStreams() throws IOException{
            //TODO Autoflush, quando escrevo algo, manda logo
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        }

        /**
         * Metodo que processa a ligacao
         * @throws IOException
         * @throws InterruptedException
         */
        private void proccessStreams() throws IOException, InterruptedException {
            // Cria o jogador e inicializa a usa posicao
            Player player = new HumanPlayer(game);
            player.start();
            System.out.println("Successful connection, starting proccessing...");
            while(true){
                sleep(Game.REFRESH_INTERVAL);
                System.out.println("Sending game status to client...");
                GameStatus sendGameStatus = new GameStatus(game);
                System.out.println(sendGameStatus);
                output.writeObject(sendGameStatus);
                System.out.println("Sent game status to client!");
                output.reset();
                //Jogo terminou, parar de enviar
                if(game.isGameEnded()){
                    closeConnection();
                }
                if(input.ready()){
                    String directionReceived = input.readLine();
                    System.out.println("DIRECTION RECEIVED !!! " + directionReceived);
                    player.setMoveDirection(Direction.valueOf(directionReceived));
                }
            }
        }

        private void closeConnection() {

            /*
            Varios try catches de modo a que caso de erro a fechar um ainda fecha os outros
             */
            try{
                input.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            try{
                output.close();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            try{
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}

