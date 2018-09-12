package place.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;

import java.awt.*;
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
    private Rectangle[][] tilesDisplay;

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
        refresh();
    }

    public void refresh(){
        for(int r = 0; r < model.getDim(); ++r){
            for(int c = 0; c < model.getDim(); ++c){
                tilesDisplay[r][c].setFill(Color.rgb(model.getContents(r, c).getColor().getRed(), model.getContents(r, c).getColor().getGreen(), model.getContents(r, c).getColor().getBlue()));
            }
        }
    }

    public void init() {
        try{
            String host = getParamNamed("host");
            int port = Integer.parseInt(getParamNamed("port"));
            username = getParamNamed("username");
            model = new ClientModel();
            this.model.addObserver(this);
            serverConn = new NetworkClient(host, port, username, model);
            serverConn.start();
            System.out.println("Connected to server "+serverConn.getSock());
            Thread.sleep(5000);
            tilesDisplay = new Rectangle[model.getDim()][model.getDim()];
        }
        catch (PlaceException |InterruptedException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void start(Stage mainstage ) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screen.getWidth();
        double height = screen.getHeight();
        double widthTile = 50;
        double heightTile = 50;
        model.addObserver(this);
        mainstage.setTitle("Place: " + username);
        GridPane tiles = new GridPane();
        tiles.setMaxHeight(height-(height/20));
        for (int x = 0; x < model.getDim(); ++x){
            for (int y =0; y < model.getDim(); ++y) {
                Color c;
                PlaceTile temp;
                temp = new PlaceTile(x, y, username, PlaceColor.WHITE);
                c =  Color.rgb(temp.getColor().getRed(), temp.getColor().getGreen(), temp.getColor().getBlue());
                tilesDisplay[x][y] = new Rectangle();
                tilesDisplay[x][y].setFill(c);
                tilesDisplay[x][y].setHeight(heightTile);
                tilesDisplay[x][y].setWidth(widthTile);
                tilesDisplay[x][y].setOnMouseClicked(event -> {
                   LocalTime time = LocalTime.now();
                   temp.setColor(color);
                   temp.setOwner(username);
                   temp.setTime(time.toNanoOfDay());
                   serverConn.sendMove(temp.getRow(), temp.getCol(), temp.getColor(), temp.getOwner());
                });
                Tooltip popup = new Tooltip();
                tilesDisplay[x][y].setOnMouseEntered(event -> {
                   Date date = new Date();
                   LocalTime time = LocalTime.now();TextField tf = new TextField();
                   popup.setText("("+temp.getRow()+","+temp.getCol()+")\n"+username+"\n"+date+"\n"+time);
                   Node n = (Node)event.getSource();
                   popup.show(n, event.getX(), event.getY());
               });
                tilesDisplay[x][y].setOnMouseExited(event -> {
                    popup.hide();
                });
               tiles.add(tilesDisplay[x][y], x, y);
            }
        }
        GridPane colors = new GridPane();
        int i = 0;
        for (PlaceColor c: PlaceColor.values()){
            StackPane s = new StackPane();
            Rectangle b = new Rectangle();
            b.setOnMouseClicked(event -> {
                color = c;
            });
            b.setFill(Color.rgb(c.getRed(),c.getGreen(),c.getBlue()));
            b.setWidth(500 / 16);
            b.setHeight(height/20);
            Text t;
            switch (c.getNumber()){
                case 10:
                    t = new Text("A");
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    break;
                case 11:
                    t = new Text("B");
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    break;
                case 12:
                    t = new Text("C");
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    break;
                case 13:
                    t = new Text("D");
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    break;
                case 14:
                    t = new Text("E");
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    break;
                case 15:
                    t = new Text("F");
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    t = new Text("R");
                    break;
                default:
                    t = new Text(""+c.getNumber());
                    s.getChildren().addAll(b, t);
                    colors.add(s, i, 0);
                    ++i;
                    break;
            }
        }
        BorderPane background = new BorderPane();
        background.setCenter(tiles);
        background.setBottom(colors);
        Scene scene = new Scene(background, width, height-100);
        mainstage.setScene(scene);
        mainstage.setMaxHeight(600);
        mainstage.setMaxWidth(515);
        mainstage.show();
    }

    public void stop() {
        serverConn.logOff();
        serverConn.close();
    }

    public static void main(String[] args) { Application.launch(args); }
}
