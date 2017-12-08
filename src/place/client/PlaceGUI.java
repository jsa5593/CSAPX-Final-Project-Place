package place.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.network.NetworkServer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.Observer;
import java.util.Observable;

public class PlaceGUI extends Application implements Observer {

    private ClientModel model;
    private NetworkServer serverConn;
    private Map<String, String> params = null;
    private String username;
    private PlaceColor color = PlaceColor.WHITE;

    private String getParamNamed( String name ) throws PlaceException {
        if ( params == null ) {
            params = super.getParameters().getNamed();
        }
        if ( !params.containsKey( name ) ) {
            throw new PlaceException(
                    "Parameter '--" + name + "=xxx' missing."
            );
        }
        else {
            return params.get( name );
        }
    }

    public void update(Observable o, Object obj){
        Platform.runLater(() -> this.update());
    }

    public void update(){
        //stuff
    }

    public void init() {
        try{
            String host = getParamNamed("host");
            int port = Integer.parseInt(getParamNamed("port"));
            username = getParamNamed("username");
            model = new ClientModel();
            // addObserver(this);
            // Create the network connection.
            this.serverConn = new NetworkServer( host, port, model, username );

            model.initializeGame();
        }
        catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(Stage mainstage ) {
        model.addObserver(this);
        mainstage.setTitle("Place: " + username);
        GridPane tiles = new GridPane();
        for (int x = 0; x < model.getDim(); ++x){
            for (int y =0; y < model.getDim(); ++y) {
               PlaceTile temp = new PlaceTile(x, y, username, PlaceColor.WHITE);
               temp.setOnMouseClicked(event -> {
                   LocalTime time = LocalTime.now();
                   temp.setColor(color);
                   temp.setOwner(username);
                   temp.setTime(time.toNanoOfDay());
                });
               temp.setOnDragOver(event -> {
                   Date date = new Date();
                   LocalTime time = LocalTime.now();
                   Tooltip popup = new Tooltip();
                   popup.setText("("+temp.getRow()+","+temp.getCol()+")\n"+username+"\n"+date+"\n"+time);
               });
               tiles.add(temp, x, y);
            }
        }
        GridPane colors = new GridPane();
        for (PlaceColor c: PlaceColor.values()){
            StackPane s = new StackPane();
            Rectangle b = new Rectangle();
            b.setOnMouseClicked(event -> {
                color = c;
            });
            //call meathod to switch color pass in "swatch"
            int i = 0;
            b.setFill(Color.rgb(c.getRed(),c.getGreen(),c.getBlue()));
            Text t;
            switch (c.getNumber()){
                case 10:
                    t = new Text("A");
                    break;
                case 11:
                    t = new Text("B");
                    break;
                case 12:
                    t = new Text("C");
                    break;
                case 13:
                    t = new Text("D");
                    break;
                case 14:
                    t = new Text("E");
                    break;
                case 15:
                    t = new Text("F");
                    break;
                default:
                    t = new Text(""+c.getNumber());
                    break;
            }
            s.getChildren().addAll(b, t);
            colors.add(s, 0, i);
            ++i;
            //setonmouseclicked
        }
    }

    public void refresh() {

    }

    public void stop() {

    }

    public static void main(String[] args) { Application.launch(args); }
}
