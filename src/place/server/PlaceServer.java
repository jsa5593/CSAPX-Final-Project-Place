package place.server;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import place.PlaceException;
import place.PlaceProtocol;
import place.client.ClientModel;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class PlaceServer implements PlaceProtocol, Closeable{

    private ServerSocket server;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

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

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java PlaceClientServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int dim = Integer.parseInt(args[1]);
        boolean listening = true;
        ClientModel model = new ClientModel();

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                System.out.println("Waiting on connection");
                new PlaceClientThread(serverSocket.accept()).start();
                model.allocate(dim);
                String user = (String)in.readUnshared();
                System.out.println("User: "+user+"Connection on port: "+portNumber);
            }
        } catch (IOException | PlaceException | ClassNotFoundException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
