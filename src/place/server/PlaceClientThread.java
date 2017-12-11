package place.server;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.NetworkClient;
import place.client.PlacePTUI;
import place.network.NetworkServer;
import place.network.PlaceRequest;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class PlaceClientThread extends Thread {

    private Socket socket;
    private int dim;
    private PlaceBoard board;
    private Scanner scannerReader;
    private PrintWriter output;
    private NetworkServer networkServer;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public PlaceClientThread(Socket socket, NetworkServer networkServer) {
        super("Place Client Thread");
        this.networkServer = networkServer;
        try{
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e){

        }
        this.socket = socket;
    }

    public void run() {
        try {
            PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
            if(req.getType() == PlaceRequest.RequestType.LOGIN){
                if(networkServer.logIn((String) req.getData(), out)){
                    System.out.println(req.getData()+" connected: "+socket.getLocalAddress()+":"+socket.getPort());
                    PlaceRequest<String> loginSuccess = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, (String)req.getData());
                    out.writeUnshared(loginSuccess);
                    out.flush();
                }
                else {
                    System.out.println("taken");
                    PlaceRequest<String>error = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Username Taken");
                    out.writeUnshared(error);
                    out.flush();
                }
            }
            else if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                System.out.println(req.getData());
                System.out.println("Change tile");
                PlaceTile newTile = (PlaceTile) req.getData();
                networkServer.makeMove(newTile);
                PlaceRequest<PlaceTile> tileChanged = new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, newTile);
                out.writeUnshared(tileChanged);
                out.flush();
            }
        }
        catch(ClassNotFoundException | IOException e) {

        }
    }
}