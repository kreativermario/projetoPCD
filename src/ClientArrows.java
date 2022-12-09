import game.RemoteClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientArrows {
    public static void main(String[] args) throws UnknownHostException {
        RemoteClient client = new RemoteClient(InetAddress.getByName("localHost"), 2022,
                37, 39, 38, 40);
        client.runClient();
        System.out.println(InetAddress.getByName("localHost"));
    }

}
