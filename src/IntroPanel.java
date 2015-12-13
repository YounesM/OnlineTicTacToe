import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Younes on 02/12/2015.
 */
public class IntroPanel extends JPanel {
    /*
        Contient l'image affichée au lancement du jeu.
     */
    public IntroPanel() {
        this.setPreferredSize(new Dimension(400,400));

    }

    @Override
    public void paintComponent(Graphics g){
        Image img = null;
        try {
            img = ImageIO.read(new File("logo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(img,0,0,this);
    }
}
