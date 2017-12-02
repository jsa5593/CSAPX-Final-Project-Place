package place.server;

import java.net.*;
import java.io.*;

public class PlaceClientThread extends Thread{

        private Socket socket = null;

        public PlaceClientThread(Socket socket) {
            super("Place Client Thread");
            this.socket = socket;
        }

        public void start(int dim){
            run(dim);
        }

        public void run(int dim) {
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));
            ) {

                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }