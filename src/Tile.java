import utils.Piece;

import javax.swing.*;

public class Tile extends JButton {

    public final int row;
    public final int column;
    private int colour;

    private int piece;

    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
        setOpaque(true);
        setBorderPainted(false);
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }

    public int getPiece() {
        return piece;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public void clear() {
        piece = Piece.EMPTY;
        colour = Piece.EMPTY;
    }
}
