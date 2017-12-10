package place.client;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlacePTUI extends ConsoleApplication implements Observer{

    private String username;
    private ClientModel model;
    private NetworkClient serverConn;
    private Scanner userIn;
    private PrintWriter userOut;
    private String host;
    private int port;

    public void init() {
        try {
            List<String> args = super.getArguments();
            username = args.get(2);
            host = args.get(0);
            port = Integer.parseInt(args.get(1));
            //this.sock = new Socket(host, port);
            this.serverConn = new NetworkClient(host, port, username);
            System.out.println("Connected to server "+serverConn.getSock());
        }
        catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.print(e);
            throw new RuntimeException(e);
        }
    }


    //---------------------takes user input and sends it to NetworkClient
    public synchronized void go (Scanner userIn, PrintWriter userOut) {
        this.userIn = userIn;
        this.userOut = userOut;
        this.model.addObserver(this);
        String[] inputs = userIn.nextLine().split(" ");
        int row = Integer.parseInt(inputs[0]);
        int col = Integer.parseInt(inputs[1]);
        PlaceColor color = PlaceColor.valueOf(inputs[2]);
        serverConn.sendMove(row, col, color, username);
        this.refresh();
    }

    public void stop() {
        //pass close and username to server
        userOut.close();
        userIn.close();
        serverConn.close();
    }

    public void refresh() {
        System.out.println("refresh");
        System.out.println(model.printBoard());
    }

    @Override
    public void update(Observable t, Object o) {
        System.out.println("update");
        assert t == this.model: "Update from non-model Observable";
        this.refresh();
    }

    public static void main(String[] args){
        ConsoleApplication.launch(PlacePTUI.class, args);}

}
