package place.client;

import place.*;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

public class NetworkClient extends Thread {

    private Socket sock;
    private PlaceBoard board;
    private ClientModel model;
    private boolean connectionOpen;
    private static HashSet<String> usernames;
    private static String user;
    private ObjectOutputStream networkOut;
    private ObjectInputStream networkIn;
    private String hostname;
    private int port;

    //pass protocol
    public NetworkClient(String hostname, int port, String user, ClientModel model) throws PlaceException {
        try {
            this.hostname = hostname;
            this.port = port;
            connectionOpen = true;
            usernames = new HashSet<>();
            this.sock = new Socket(hostname, port);
            networkOut = new ObjectOutputStream(sock.getOutputStream());
            networkOut.flush();
            networkIn = new ObjectInputStream(sock.getInputStream());
            this.user = user;
            this.model = model;
            PlaceRequest<String> login = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, user);
            networkOut.writeUnshared(login);
            networkOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSock() {
        return sock;
    }

    public void run() {
        while (connectionOpen) {
            try {
                PlaceRequest<?> req = (PlaceRequest<?>) networkIn.readUnshared();
                if (req.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                } else if (req.getType() == PlaceRequest.RequestType.ERROR) {
                    this.close();
                    break;
                } else if (req.getType() == PlaceRequest.RequestType.BOARD) {
                    this.board = (PlaceBoard) req.getData();
                    this.model.allocate(this.board);
                } else if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    PlaceTile changedTile = (PlaceTile) req.getData();
                    model.makeMove(changedTile);
                }
            }
            catch (PlaceException | ClassNotFoundException | IOException e) {
                System.exit(-1);
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            this.networkIn.close();
            this.networkOut.close();
        } catch (IOException e) {

        }
    }

    public void logOff() {
        PlaceRequest<String> exit = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, user);
        try {
            networkOut.writeUnshared(exit);
            networkOut.flush();
            networkOut.close();
            networkIn.close();
            this.close();
        }
        catch (IOException e) {

        }

    }

    public synchronized void sendMove(int r, int c, PlaceColor color, String un) {
        PlaceTile tile = new PlaceTile(r, c, un, color);
        PlaceRequest<PlaceTile> changeTile = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, tile);
        try {
            networkOut.writeUnshared(changeTile);
            networkOut.flush();
        } catch (IOException e) {

        }
    }
}