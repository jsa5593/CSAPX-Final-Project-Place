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
    private PlaceBoard model;
    private Boolean go;
    private static final Boolean DEBUG = false;
    private boolean connectionOpen;
    private static HashSet<String> usernames;
    private static String user;
    private ObjectOutputStream networkOut;
    private ObjectInputStream networkIn;

    //pass protocol
    public NetworkClient(String hostname, int port, String user) throws PlaceException{
        try{
            connectionOpen = true;
            System.out.println("network client");
            usernames = new HashSet<>();
            this.sock = new Socket(hostname, port);
            System.out.println("network client sock");
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

            networkIn = new ObjectInputStream(sock.getInputStream());
            System.out.println("network in");

            PlaceRequest<?> req = (PlaceRequest<?>) networkIn.readUnshared();
            System.out.println("req" +req);
            if(req.getType() == PlaceRequest.RequestType.LOGIN_SUCCESS){
                System.out.println("Success");
            }

            Thread netThread = new Thread(() -> this.run());
            netThread.start();
        }
        catch (IOException | ClassNotFoundException e){
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
        while (connectionOpen) {
            try {


                NetworkClient.dPrint("Connected to server "+this.sock);
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

        }
        catch (IOException e) {

        }

    }
}
