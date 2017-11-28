package place.client;

import place.PlaceException;
import sun.nio.ch.Net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import place.PlaceProtocol;

import static place.PlaceProtocol.*;

public class NetworkClient {

    private Socket sock;
    private Scanner networkIn;
    private PrintStream networkOut;
    private ClientModel model;
    private Boolean go;
    private static final Boolean DEBUG = false;

    public NetworkClient(String hostname, int port, ClientModel model) throws PlaceException{
        try{
            this.sock = new Socket(hostname, port);
            this.networkIn = new Scanner(sock.getInputStream());
            this.networkOut = new PrintStream(sock.getOutputStream());
            this.model = model;
            this.go = true;
            String request = this.networkIn.next();
            String arguments = this.networkIn.nextLine();
            assert request.equals(PlaceProtocol.CONNECT);
            NetworkClient.dPrint("Connected to server "+this.sock);
            this.connect(arguments);
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
            String request = this.networkIn.next();
            String arguments = this.networkIn.nextLine().trim();
            NetworkClient.dPrint("Net message in = \"" + request + '"');

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
        this.model.allocate(Integer.parseInt(fields[0]));
    }

    private static void dPrint(Object logMsg){
        if(NetworkClient.DEBUG){
            System.out.println(logMsg);
        }
    }
}
