import game.RemoteClient;
import game.Server;
import gui.ClientGUI;
import gui.GameGuiMain;

public class runProgram {

    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }

}
