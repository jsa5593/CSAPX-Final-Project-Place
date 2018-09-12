package place.server;

import place.PlaceException;
import place.PlaceProtocol;
import place.network.NetworkServer;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PlaceServer implements PlaceProtocol, Closeable {

    private ServerSocket server;

    public PlaceServer(int port) throws PlaceException {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    public void close() {
        try {
            this.server.close();
        } catch (IOException e) {

        }
    }

    public void run(int dim, int portNumber, boolean listening) {
        try {
            NetworkServer networkServer = new NetworkServer(dim);
            while (listening) {
                Socket sock = server.accept();
                PlaceClientThread client = new PlaceClientThread(sock, networkServer);
                client.start();
            }
        } catch (PlaceException | IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java PlaceClientServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int dim = Integer.parseInt(args[1]);
        boolean listening = true;

        try {
            PlaceServer server = new PlaceServer(portNumber);
            server.run(dim, portNumber, listening);
        } catch (PlaceException e) {
            System.err.println("Can't connect to server");
            e.printStackTrace();
        }

    }
}
