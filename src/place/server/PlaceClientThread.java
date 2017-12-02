package place.server;

import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static place.PlaceProtocol.*;

public class PlaceClientThread extends Thread{

        private Socket socket = null;
        private HashSet<String> usernames;

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
                boolean go = true;
                while (go) {//run while screen is open
                    String[] user = in.readLine().split(" ");
                    if (user[0].equals(LOGIN)) {
                        if (!usernames.contains(user[1])) {
                            usernames.add(user[1]);
                            out.println(LOGIN_SUCCESSFUL + user[1]);
                            //send initial board
                        } else {
                            out.println(ERROR + "username taken");
                            socket.close();
                        }
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}