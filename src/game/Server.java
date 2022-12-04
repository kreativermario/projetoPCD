
package game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private ServerSocket server;
    protected  Game game;
    public static final int PORT = 2022;

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
            } catch (IOException e) {
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

        public void proccessConnection() throws IOException{
            System.out.println("Successful connection, starting proccessing...");
            Player player = new HumanPlayer(game);
            player.start();

            while(true){
                String message = input.readLine();
                if(message != null){
                    //TODO do movement!

                }
                output.writeObject(game);

                //output.println("Echo: " + message);
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

