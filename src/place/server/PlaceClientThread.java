package place.server;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.NetworkClient;
import place.client.PlacePTUI;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class PlaceClientThread extends Thread {

    private Socket socket;
    private int dim;
    private PlaceBoard board;
    private Scanner scannerReader;
    private PrintWriter output;
    private ObjectInputStream in;

    public PlaceClientThread(Socket socket) {
        super("Place Client Thread");
        try{
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e){

        }
        System.out.println("makes client thread");
        this.socket = socket;
    }

    public void run() {
        try  {
            boolean go = true;
            while (go) {

            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}