package place.client;

import place.*;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import static place.PlaceProtocol.*;

public class NetworkClient {

    private Socket sock;
    private PrintWriter toThread;
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
            System.out.println("network client");
            usernames = new HashSet<>();
            this.sock = new Socket(hostname, port);
            System.out.println("network client sock: "+sock);
            this.user = user;
            this.go = true;

            System.out.println("b4 obj streams");
            networkOut = new ObjectOutputStream(sock.getOutputStream());
            networkOut.flush();
            System.out.println("obj out streams");

            //Sending username
            PlaceRequest<String> login = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN, user);
            System.out.println(login);
            networkOut.writeUnshared(login);
            networkOut.flush();

            System.out.println("send username");

            run();
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

    private void run(){
        System.out.println("run method");
        try {
            this.sock = new Socket(hostname, port);
            System.out.println(sock);
            networkOut = new ObjectOutputStream(sock.getOutputStream());
            networkOut.flush();
            networkIn = new ObjectInputStream(sock.getInputStream());
            System.out.println("network in");
            while (connectionOpen) {

                try {
                    PlaceRequest<?> req = (PlaceRequest<?>) networkIn.readUnshared();
                    System.out.println("req" +req);
                    if(req.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS){
                        System.out.println("Success");
                    }
                    else if (req.getType() == PlaceRequest.RequestType.BOARD){
                        System.out.println("Board received");
                    }

                    else if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED) {
                        PlaceTile changedTile = (PlaceTile)req.getData();
                        board.setTile(changedTile);

                    }
                    else if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {

                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        catch(IOException e) {

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
    }

    public synchronized void sendMove(int r, int c, PlaceColor color, String un) {
        //this.networkOut.println(new PlaceTile(r,c,un,color));
        System.out.println("send move");
        PlaceTile tile = new PlaceTile(r, c, un, color);
        try {
            networkOut.writeUnshared(tile);
            networkOut.flush();
        }
        catch (IOException e) {

        }

    }
}
