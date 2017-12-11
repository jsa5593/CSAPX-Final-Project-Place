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
import java.util.HashMap;
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
    private static HashMap<String, ObjectOutputStream> clientOuts;


    public NetworkServer(Socket sock, int dim) throws PlaceException{
        clientOuts = new HashMap<>();
        try{
            this.networkOut = new ObjectOutputStream(sock.getOutputStream());
            this.sock = sock;
            model = new PlaceBoard(dim);
            networkOut.writeUnshared(model);
            this.go = true;
        }
        catch (IOException e){
            throw new PlaceException(e);
        }
    }

    public void makeMove(PlaceTile tile){
        model.setTile(tile);
    }

    public synchronized boolean logIn(String username, ObjectOutputStream out) {
        if (clientOuts.containsKey(username)) {
            return false;
        }
        else {
            clientOuts.put(username, out);
            System.out.println(model);
            PlaceRequest<PlaceBoard> board = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, model);
            for (ObjectOutputStream o: clientOuts.values()){
                try {
                    o.writeUnshared(board);
                    o.flush();
                }
                catch (IOException e){

                }
            }
            return true;
        }

    }

    private synchronized boolean goodToGo(){
        return this.go;
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
