import javax.swing.*;
import java.net.Socket;

/**
 * Created by Younes on 02/12/2015.
 */
public class GameFrame extends JFrame{
    /*
        Frame contenant la grille
     */
    private GamePanel gamePanel;

    public GameFrame(Socket socket,MainFrame parent) {
        gamePanel = new GamePanel(this,socket);
        this.setTitle("Game");
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.add(gamePanel);
        this.pack();
    }
}
