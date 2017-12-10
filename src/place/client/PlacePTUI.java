package place.client;

import place.PlaceBoard;
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
    private ClientModel board;
    private NetworkClient serverConn;
    private Scanner userIn;
    private PrintWriter userOut;
    private String host;
    private int port;
    private Socket sock;

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

    public synchronized void go (Scanner userIn, PrintWriter userOut) {
        this.userIn = userIn;
        this.userOut = userOut;
//        this.board.addObserver(this);
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
        //this.userOut.println(this.board);
        //this.userOut.println("Enter row, column, and color: ");
        //this.userOut.flush();
        //String input = userIn.nextLine();
        //userOut.println(input+" "+username);

        //System.out.println(input);
        /*String color = this.userIn.nextLine();
        if (this.board.isValid(row, col, color)) {
            this.userOut.println(this.userIn.nextLine());
            this.serverConn.sendMove(row, col, color, username);
        }*/
    }

    @Override
    public void update(Observable t, Object o) {
        System.out.println("update");
        assert t == this.board: "Update from non-model Observable";
        this.refresh();
    }

    public static void main(String[] args){
        ConsoleApplication.launch(PlacePTUI.class, args);}

}
