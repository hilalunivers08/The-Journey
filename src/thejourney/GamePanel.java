package thejourney;

import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JPanel;

public class GamePanel extends JPanel{

    //SCREEN SETTINGS
    final int originalTileSize = 16; //16x16 tile size

    //scale
    final int scale = 3;

    final int tileSize = originalTileSize * scale;  // 48x48 tiles

    //max screen coloumn & row

    final int maxScreenCol = 16;
    final int maxScreenRow = 12 ;

    final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    public GamePanel() {
        this.setPreferredSize (new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
    }
}