package place.server;

import place.PlaceBoard;
import place.PlaceException;
import place.PlaceProtocol;
import place.PlaceTile;
import place.network.PlaceRequest;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class PlaceServer implements PlaceProtocol, Closeable{

    private ServerSocket server;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static PlaceBoard model;
    private static HashSet<String> usernames;
    private static PlaceTile changeTile;
    private static HashMap<String, ObjectOutputStream> clientOuts;
    public PlaceServer(int port) throws PlaceException{
        try {
            this.server = new ServerSocket(port);
        }
        catch (IOException e){
            throw new PlaceException(e);
        }
    }

    public void close(){
        try{
            this.server.close();
        }
        catch (IOException e){

        }
    }

    public void run(int dim, int portNumber, boolean listening){
        try {
            System.out.println("try");

            System.out.println(server);
            in = new ObjectInputStream(server.accept().getInputStream());
            System.out.println("obj in");
            System.out.println(server.accept());
            while (listening) {
                System.out.println("Waiting on connection");
                //Get login
                PlaceRequest<?> req = (PlaceRequest<?>) in.readUnshared();
                System.out.println("req"+req);
                if(req.getType() == PlaceRequest.RequestType.LOGIN){
                    System.out.println(req.getData());
                    if(!usernames.contains(req.getData())){
                        Socket sock = new Socket(InetAddress.getLocalHost(), portNumber);
                        System.out.println(sock);
                        out = new ObjectOutputStream(sock.getOutputStream());
                        out.flush();
                        System.out.println("obj out");
                        usernames.add((String)req.getData());
                        System.out.println("username: "+req.getData());
                        clientOuts.put((String)req.getData(), new ObjectOutputStream(sock.getOutputStream()));
                        PlaceRequest<String> loginSuccess = new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS, (String)req.getData());
                        out.writeUnshared(loginSuccess);
                        System.out.println("loginsuccess "+loginSuccess);
                        out.flush();
                        model = new PlaceBoard(dim);
                        System.out.println("model"+model);
                        PlaceRequest<PlaceBoard> board = new PlaceRequest<>(PlaceRequest.RequestType.BOARD, model);
                        out.writeUnshared(board);
                        out.flush();
                        PlaceClientThread thread = new PlaceClientThread(server.accept());
                        thread.start();
                    }
                    else {
                        PlaceRequest<String>error = new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Username Taken");
                        out.writeUnshared(error);
                        out.flush();
                    }
                }
                else if (req.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile newTile = (PlaceTile) req.getData();
                    model.setTile(newTile);
                    PlaceRequest<PlaceTile> tileChanged = new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, newTile);
                    out.writeUnshared(tileChanged);
                    out.flush();
                    //thread.sleep(500);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java PlaceClientServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int dim = Integer.parseInt(args[1]);
        boolean listening = true;

        usernames = new HashSet<>();
        clientOuts = new HashMap<>();

        try {
            PlaceServer server = new PlaceServer(portNumber);
            server.run(dim, portNumber, listening);
        }
        catch (PlaceException e){
            System.err.println("Can't connect to server");
            e.printStackTrace();
        }

    }
}
