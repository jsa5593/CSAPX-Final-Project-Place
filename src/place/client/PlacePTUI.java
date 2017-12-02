package place.client;

import place.PlaceException;
import place.PlaceProtocol;

import java.io.PrintWriter;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.*;

public class PlacePTUI extends ConsoleApplication implements Observer{

    private String username;
    private ClientModel board;
    private NetworkClient serverConn;
    private Scanner userIn;
    private PrintWriter userOut;

    public void init() {
        try {
            List<String> args = super.getArguments();
            username = args.get(2);
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
            this.board = new ClientModel();
            this.serverConn = new NetworkClient(host, port, this.board, username);
            this.board.initializeGame();
        }
        catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
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

}
