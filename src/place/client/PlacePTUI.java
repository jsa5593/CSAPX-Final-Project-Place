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

    @Override
    public void init() {
        try {
            List<String> args = super.getArguments();
            username = args.get(2);
            host = args.get(0);
            port = Integer.parseInt(args.get(1));
            model = new ClientModel();
            this.serverConn = new NetworkClient(host, port, username);
            new Thread(() -> {
                serverConn.run();
            }).start();
        }
        catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.print(e);
            throw new RuntimeException(e);
        }
    }


    //---------------------takes user input and sends it to NetworkClient
    @Override
    public synchronized void go (Scanner userIn, PrintWriter userOut) {
        this.userIn = userIn;
        this.userOut = userOut;
        this.model.addObserver(this);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
        }
        this.refresh();
    }

    public synchronized void run() {

        this.model.addObserver(this);
        System.out.println("GO");

        this.refresh();
    }

    @Override
    public void stop() {
        //pass close and username to server
        userOut.close();
        userIn.close();
        serverConn.close();
    }


    private void refresh() {
        System.out.println("refresh");
        while (true) {
            System.out.println(model.printBoard());
            System.out.println("Enter row col color or -1 to exit: ");
            String[] inputs = userIn.nextLine().split(" ");
            System.out.println(inputs);
            if (inputs.length == 1) {
                break;
            } else if (inputs.length == 3) {
                int row = Integer.parseInt(inputs[0]);
                int col = Integer.parseInt(inputs[1]);
                System.out.println(Integer.valueOf(inputs[2], 16).intValue());
                PlaceColor color = PlaceColor.values()[Integer.decode(inputs[2])];
                serverConn.sendMove(row, col, color, username);
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void update(Observable t, Object o) {
        System.out.println("update");
        assert t == this.model: "Update from non-model Observable";
        this.refresh();
    }

    public static void main(String[] args){
        ConsoleApplication.launch(PlacePTUI.class, args);
        System.out.println("launch method in ptui");}

}
