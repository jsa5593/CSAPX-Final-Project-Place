package place.server;

import place.client.ClientModel;
import place.network.PlaceRequest;

import java.net.*;
import java.io.*;

public class PlaceClientThread extends Thread{

        private Socket socket = null;
        private int dim;
        private ClientModel board;

        public PlaceClientThread(Socket socket) {
            super("Place Client Thread");
            this.socket = socket;
        }

        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                boolean go = true;
                while (go) {//run while screen is open

                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}