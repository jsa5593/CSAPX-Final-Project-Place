package place.server;

import place.PlaceTile;
import place.network.NetworkServer;
import place.network.PlaceRequest;
import java.net.*;
import java.io.*;

public class PlaceClientThread extends Thread {

    private Socket socket;
    private NetworkServer networkServer;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public PlaceClientThread(Socket socket, NetworkServer networkServer) {
        super("Place Client Thread");
        this.networkServer = networkServer;

        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {

        }
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
            if (req.getType() == PlaceRequest.RequestType.LOGIN) {
                if (networkServer.logIn((String) req.getData(), out)) {
                    System.out.println(req.getData() + " connected: "+socket.getInetAddress()+":"+socket.getPort());
                    while (true) {
                        PlaceRequest<?> reqTile = (PlaceRequest<?>) in.readUnshared();
                        if (reqTile.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                            PlaceTile newTile = (PlaceTile) reqTile.getData();
                            networkServer.makeMove(newTile);
                        }
                        else if (reqTile.getType() == PlaceRequest.RequestType.ERROR) {
                            networkServer.logOff((String) reqTile.getData());
                            return;
                        }
                    }
                }
                else {
                    //networkServer.close();
                }

            }
            else if (req.getType() == PlaceRequest.RequestType.ERROR) {
                networkServer.logOff((String) req.getData());
            }
        } catch (ClassNotFoundException | IOException e) {
            System.exit(-1);
        }
    }

}