import javax.swing.*;

public class Tile extends JButton {

    public final int row;
    public final int column;

    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
        setOpaque(true);
        setBorderPainted(false);
    }
}
