package place.client;

import javafx.application.Application;
import place.PlaceException;
import place.PlaceProtocol;
import place.network.NetworkServer;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.*;

public class PlacePTUI extends ConsoleApplication implements Observer{

    private String username;
    private ClientModel board;
    private NetworkServer serverConn;
    private Scanner userIn;
    private PrintWriter userOut;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket sock;

    public void init() {
        try {
            System.out.println("init");
            List<String> args = super.getArguments();
            username = args.get(2);
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
            System.out.println(port);
            sock = new Socket(host, port);
            input = new ObjectInputStream(sock.getInputStream());
            output = new ObjectOutputStream(sock.getOutputStream());
            this.serverConn = new NetworkServer(host, port, username);
            System.out.println("serverConn");
            PlaceRequest<?> req = (PlaceRequest<?>)input.readObject();
            if (req.getType() == PlaceRequest.RequestType.BOARD){
                this.board = (ClientModel)req.getData();
            }
        }
        catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException | IOException | ClassNotFoundException e) {
            System.out.print(e);
            throw new RuntimeException(e);
        }
    }

    public synchronized void go (Scanner userIn, PrintWriter userOut) {
        this.userIn = userIn;
        this.userOut = userOut;
        this.board.addObserver(this);
        this.refresh();
    }

    public void stop() {
        userOut.close();
        userIn.close();
        serverConn.close();
    }

    public void refresh() {
        this.userOut.println(this.board);
        this.userOut.println("Enter row, column, and color: ");
        this.userOut.flush();
        int row = this.userIn.nextInt();
        int col = this.userIn.nextInt();
        String color = this.userIn.nextLine();
        if (this.board.isValid(row, col, color)) {
            this.userOut.println(this.userIn.nextLine());
            this.serverConn.sendMove(row, col, color, username);
        }
    }

    @Override
    public void update(Observable t, Object o) {
        assert t == this.board: "Update from non-model Observable";
        this.refresh();
    }

    public static void main(String[] args){
        ConsoleApplication.launch(PlacePTUI.class, args);}

}
