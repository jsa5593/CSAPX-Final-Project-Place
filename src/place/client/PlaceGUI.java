package place.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.Observer;
import java.util.Observable;

public class PlaceGUI extends Application implements Observer {

    private ClientModel model;
    private Map<String, String> params = null;
    private String username;
    private PlaceColor color = PlaceColor.WHITE;
    private NetworkClient serverConn;
    private PlaceTile[][] tilesDisplay;

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

    public void refresh(){
        for(int r = 0; r < model.getDim(); ++r){
            for(int c = 0; c < model.getDim(); ++c){
                tilesDisplay[r][c] = model.getContents(r, c);
            }
        }
    }

    public void init() {
        try{
            String host = getParamNamed("host");
            int port = Integer.parseInt(getParamNamed("port"));
            username = getParamNamed("username");
            serverConn = new NetworkClient(host, port, username);
            model = new ClientModel();
            tilesDisplay = new PlaceTile[model.getDim()][model.getDim()];
            System.out.println("Connected to server "+serverConn.getSock());
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
                PlaceTile temp;
                temp = new PlaceTile(x, y, username, PlaceColor.WHITE);
                tilesDisplay[x][y] = temp;
                temp.setOnMouseClicked(event -> {
                   LocalTime time = LocalTime.now();
                   temp.setColor(color);
                   temp.setOwner(username);
                   temp.setTime(time.toNanoOfDay());
                   serverConn.sendMove(temp.getRow(), temp.getCol(), temp.getColor(), temp.getOwner());
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
        BorderPane background = new BorderPane();
        background.setCenter(tiles);
        background.setBottom(colors);
        Scene scene = new Scene(background, 500, 500);
        mainstage.setScene(scene);
        mainstage.show();

    }

    public void stop() {

        serverConn.close();
    }

    public static void main(String[] args) { Application.launch(args); }
}
