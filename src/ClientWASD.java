import game.RemoteClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientWASD {
    public static void main(String[] args) throws UnknownHostException {
        RemoteClient client = new RemoteClient(InetAddress.getByName("localHost"), 2022,
                65, 68, 87, 83);
        client.runClient();
    }

}
