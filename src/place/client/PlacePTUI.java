package place.client;

import place.PlaceColor;
import place.PlaceException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlacePTUI implements Observer {

    private static String username;
    private ClientModel model;
    private NetworkClient serverConn;
    private Scanner userIn;
    private static String host;
    private static int port;
    private long id;

    public void init() {
        try {
            model = new ClientModel();
            this.model.addObserver(this);
            this.serverConn = new NetworkClient(host, port, username, model);
            serverConn.start();
            System.out.println("Connected to server "+serverConn.getSock());
            id = Thread.currentThread().getId();
            go(new Scanner(System.in));
        } catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.print(e);
            throw new RuntimeException(e);
        }
    }

    public synchronized void go(Scanner userIn) {
        this.userIn = userIn;
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.refresh();
        this.getInput();
    }

    public void stop() {
        serverConn.logOff();
        serverConn.close();
    }


    private void refresh() {
        System.out.println(model.printBoard());
    }

    private void getInput() {
        while (true) {
            System.out.println("Enter row col color(0-15)or R for random color or -1 to exit: ");
            if(Thread.currentThread().getId() == id){
                String in = userIn.nextLine().trim();
                String[] inputs = in.split(" ");
                if (inputs.length == 1) {
                    if(inputs[0].equals("-1")) {
                        stop();
                        break;
                    }
                }
                else if (inputs.length == 3) {
                    int num;
                    if(inputs[2].equals("R")){
                        num = (int)(Math.random()*16);
                    }
                    else {
                        num = Integer.parseInt(inputs[2]);
                    }
                    int row = Integer.parseInt(inputs[0]);
                    int col = Integer.parseInt(inputs[1]);
                    PlaceColor color = PlaceColor.values()[Integer.decode(String.valueOf(num))];
                    serverConn.sendMove(row, col, color, username);
                    try {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {

                }
            }
            else {
                break;
            }
        }
    }


    @Override
    public void update(Observable t, Object o) {
        this.refresh();
    }

    public static void main(String[] args) {
        //ConsoleApplication.launch(PlacePTUI.class, args);
        host = args[0];
        port = Integer.parseInt(args[1]);
        username = args[2];
        PlacePTUI ptui = new PlacePTUI();
        ptui.init();
    }

}
