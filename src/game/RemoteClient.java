package game;

import gui.ClientGUI;
import gui.GameGuiMain;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class RemoteClient {
    private Socket socket;
    private ObjectInputStream input;
    private PrintWriter output;

    private String hostName;
    public static final int PORT = 2022;

    public RemoteClient(String hostName){
        this.hostName = hostName;
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
            socket = new Socket(InetAddress.getByName(hostName), 2022);
            System.err.println("Client " + hostName + " CONNECTED TO SERVER!");
        } catch (IOException e) {
            System.err.println("Client " + hostName + " error connecting... exiting");
            System.exit(1);
        }
    }

    public void getStreams() throws IOException{
        //TODO Autoflush, quando escrevo algo, manda logo
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new ObjectInputStream(socket.getInputStream());

    }

    public void proccessConnection() throws IOException {

        try {
            Game game = (Game) input.readObject();
            ClientGUI clientGuiMain = new ClientGUI(game);
            clientGuiMain.init();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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

}

