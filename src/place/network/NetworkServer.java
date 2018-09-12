package place.network;

import place.PlaceBoard;
import place.PlaceException;
import place.PlaceTile;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class NetworkServer {

    private PlaceBoard model;
    private static HashMap<String, ObjectOutputStream> clientOuts = new HashMap<>();


    public NetworkServer(int dim) throws PlaceException {
        model = new PlaceBoard(dim);
    }

    public synchronized void makeMove(PlaceTile tile) {
        model.setTile(tile);
        PlaceRequest<PlaceTile> tileChanged = new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, tile);
        for (String s : clientOuts.keySet()) {
            try {
                clientOuts.get(s).writeUnshared(tileChanged);
                clientOuts.get(s).flush();
            } catch (IOException e) {

            }
        }
    }

    public synchronized boolean logIn(String username, ObjectOutputStream networkOut) {
        if (clientOuts.containsKey(username)) {
            PlaceRequest<String> error = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, username);
            try {
                networkOut.writeUnshared(error);
                networkOut.flush();
                return false;
            } catch (IOException e) {
                System.exit(-1);
            }
        } else {
            clientOuts.put(username, networkOut);
            try {
                PlaceRequest<String> loginSuccess = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, username);
                clientOuts.get(username).writeUnshared(loginSuccess);
                clientOuts.get(username).flush();
            }
            catch (IOException e) {
                System.exit(-1);
            }
            PlaceRequest<PlaceBoard> board = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, model);
            try {
                clientOuts.get(username).writeUnshared(board);
                clientOuts.get(username).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return true;
    }

    public void logOff(String username) {
        try {
            clientOuts.get(username).close();
            clientOuts.remove(username);
            System.out.println("Logging off "+username);
        }
        catch (IOException e ) {

        }

    }
}
