package place.server;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import place.PlaceException;
import place.PlaceProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PlaceServer implements PlaceProtocol, Closeable{

    private ServerSocket server;

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

        if (args.length != 1) {
            System.err.println("Usage: java KKMultiServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new PlaceClientThread(serverSocket.accept()).start(Integer.parseInt(args[1]));
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
