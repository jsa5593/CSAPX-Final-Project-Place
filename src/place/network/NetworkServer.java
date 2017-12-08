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
    private ClientModel model;
    private Boolean go;
    private static final Boolean DEBUG = false;
    private static HashSet<String> usernames;

    public NetworkServer(String hostname, int port, String user) throws PlaceException{
        try{
            usernames = new HashSet<>();
            this.sock = new Socket(hostname, port);
            System.out.println("socket");
            System.out.println("NetworkServer try");
            this.networkIn = new ObjectInputStream(sock.getInputStream());
            System.out.println("object input");
            this.networkOut = new ObjectOutputStream(sock.getOutputStream());
            System.out.println(user);
            System.out.println(usernames);
            if (!usernames.contains(user)) {
                System.out.println("NetworkServer !contain");
                usernames.add(user);
                networkOut.writeUnshared(user);
                System.out.println("write user");
                networkOut.flush();
                System.out.println("flush");
            }
            else {
                System.err.print("Username taken");
                System.exit(-1);
            }
            System.out.println("scanner/print");
            this.model = new ClientModel();
            PlaceRequest<PlaceBoard> boardReq = new PlaceRequest<PlaceBoard>(PlaceRequest.RequestType.BOARD, model);
            this.model.initializeGame();
            networkOut.writeObject(boardReq);
            networkOut.flush();
            System.out.println(model);
            System.out.println("model");
            this.go = true;
            networkOut.writeUnshared(LOGIN+user);
            Thread netThread = new Thread(() -> this.run());
            netThread.start();
        }
        catch (IOException e){
            throw new PlaceException(e);
        }
    }

    private synchronized boolean goodToGo(){
        return this.go;
    }

    private void run(){
        try {
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
        this.model.close();
    }

    public void sendMove(int r, int c, String color, String username) {
        //this.networkOut.println(new PlaceTile(r,c,un,color));
    }
}
