package place.client;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;

import java.awt.*;
import java.util.Observable;

public class ClientModel extends Observable{

    public enum colors{
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, A, B, C, D, E, F,
    }

    private PlaceBoard board;
    //private ClientModel.colors[][] model;
    private int dim;
    private final static int MINDIM = 5;

    public int getDim(){
        return dim;
    }

    public void allocate(int dim) throws  PlaceException{
        if(dim < MINDIM){
            throw new PlaceException("Board too small");
        }
        //this.model = new ClientModel.colors[dim][dim];
        this.dim = dim;
        board = new PlaceBoard(dim);
    }

    public void close(){
        super.setChanged();
        super.notifyObservers();
    }

    public String printBoard(){
        return board.toString();
    }

    public void makeMove(PlaceTile tile){
        int row = tile.getRow();
        int col = tile.getCol();
        PlaceColor color = tile.getColor();
        String user = tile.getOwner();
        Long time = tile.getTime();
        board.setTile(tile);
        super.notifyObservers();
    }

    public PlaceTile getContents(int row, int col){return this.board.getTile(row, col);}

}