package place.network;

import place.PlaceBoard;
import place.PlaceException;
import place.client.*;
import place.PlaceTile;
import sun.nio.ch.Net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import place.PlaceProtocol;

import static place.PlaceProtocol.*;

public class NetworkServer {

    private Socket sock;
    private ObjectInputStream networkIn;
    private ObjectOutputStream networkOut;
    private PlaceBoard model;
    private Boolean go;
    private int dim;
    private static final Boolean DEBUG = false;

    public NetworkServer(String hostname, int port, String user) throws PlaceException{
        try{
            this.sock = new Socket(hostname, port);
            this.networkIn = new ObjectInputStream(sock.getInputStream());
            this.networkOut = new ObjectOutputStream(sock.getOutputStream());
            model = (PlaceBoard)networkIn.readUnshared();
            networkOut.writeUnshared(model);
            this.go = true;
            networkOut.writeUnshared(LOGIN+user);
            Thread netThread = new Thread(() -> this.run());
            netThread.start();
        }
        catch (IOException | ClassNotFoundException e){
            throw new PlaceException(e);
        }
    }

    private synchronized boolean goodToGo(){
        return this.go;
    }

    private void run(){
        try {
            PlaceRequest<?> req = (PlaceRequest<?>) networkIn.readObject();
            if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                PlaceTile tile = (PlaceTile) req.getData();
            }
            else if (req.getType() == PlaceRequest.RequestType.TILE_CHANGED){

            }

            else if (req.getType() == PlaceRequest.RequestType.BOARD) {

            }


            String request = (String)this.networkIn.readUnshared();
            String arguments = (String)this.networkIn.readUnshared();
            arguments = arguments.trim();
            assert request.equals(PlaceProtocol.CONNECT);
            NetworkServer.dPrint("Connected to server "+this.sock);
            this.connect(arguments);
            NetworkServer.dPrint("Net message in = \"" + request + '"');

            switch(request) {
                case (LOGIN):
                    break;
                case (LOGIN_SUCCESSFUL):
                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String arguments) throws PlaceException{
        String fields[] = arguments.trim().split(" ");
    }

    private static void dPrint(Object logMsg){
        if(NetworkServer.DEBUG){
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

    public synchronized void sendMove(int r, int c, String color, String username) {
        //this.networkOut.println(new PlaceTile(r,c,un,color));
    }
}
