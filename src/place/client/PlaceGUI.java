package place.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import place.PlaceException;

import java.util.Map;
import java.util.Observer;
import java.util.Observable;

public class PlaceGUI extends Application implements Observer {

    private ClientModel model;
    private NetworkClient serverConn;
    private Map<String, String> params = null;

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

            model = new ClientModel();
            // addObserver(this);
            // Create the network connection.
            this.serverConn = new NetworkClient( host, port, model );

            model.initializeGame();
        }
        catch (PlaceException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
    public void start(Stage mainstage ) {
    }
}
