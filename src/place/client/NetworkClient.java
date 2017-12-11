package place.client;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import place.*;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import static place.PlaceProtocol.*;

public class NetworkClient {

    private Socket sock;
    private PlaceBoard board;
    private ClientModel model;
    private Boolean go;
    private static final Boolean DEBUG = false;
    private boolean connectionOpen;
    private static HashSet<String> usernames;
    private static String user;
    private ObjectOutputStream networkOut;
    private ObjectInputStream networkIn;
    private String hostname;
    private int port;

    //pass protocol
    public NetworkClient(String hostname, int port, String user) throws PlaceException{
        try{
            this.hostname = hostname;
            this.port = port;
            connectionOpen = true;
            usernames = new HashSet<>();
            this.sock = new Socket(hostname, port);
            this.user = user;
            this.go = true;
            this.model = new ClientModel();
            networkOut = new ObjectOutputStream(sock.getOutputStream());
            networkOut.flush();
            networkIn = new ObjectInputStream(sock.getInputStream());
            PlaceRequest<String> login = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, user);
            networkOut.writeUnshared(login);
            networkOut.flush();
        }
        catch (IOException e){
            throw new PlaceException(e);
        }
    }

    public Socket getSock(){
        return sock;
    }

    private synchronized boolean goodToGo(){
        return this.go;
    }

    public void run(){
        while (connectionOpen) {
            try {
                connectionOpen = false;
                System.out.println("run");
                PlaceRequest<?> req = (PlaceRequest<?>) networkIn.readUnshared();
                System.out.println(req);
                if (req.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS) {
                    System.out.println("login Success");
                }
                else if (req.getType() == PlaceRequest.RequestType.BOARD) {
                    this.board = new PlaceBoard(((PlaceBoard) req.getData()).DIM);
                    this.model.allocate(this.board);
                }
                else if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                    PlaceTile changedTile = (PlaceTile) req.getData();
                    board.setTile(changedTile);
                }
                else if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.close();
    }

    public void connect(String arguments) throws PlaceException{
        String fields[] = arguments.trim().split(" ");
    }

    private static void dPrint(Object logMsg){
        if(NetworkClient.DEBUG){
            System.out.println(logMsg);
        }
    }

    public void close() {
        try {
            //logoff
            this.sock.close();
        }
        catch (IOException e) {

        }
        this.model.close();
    }

    public PlaceBoard getBoard() {
        return board;
    }

    public synchronized void sendMove(int r, int c, PlaceColor color, String un) {
        //this.networkOut.println(new PlaceTile(r,c,un,color));
        System.out.println("send move");
        PlaceTile tile = new PlaceTile(r, c, un, color);
        PlaceRequest<PlaceTile> changeTile = new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, tile);
        try {
            System.out.println(changeTile);
            networkOut.writeUnshared(changeTile);
            networkOut.reset();
            networkOut.flush();
            System.out.println("sent");
        }
        catch (IOException e) {

        }

    }
}
