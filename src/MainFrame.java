import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Younes on 01/12/2015.
 */
public class MainFrame extends JFrame{
    /*
        Frame principale du jeu, offre deux champs pour indiquer l'IP et le numéro de port du serveur
     */
    private IntroPanel introPanel = new IntroPanel();
    private JTextField ipField = new JTextField();
    private JTextField portField = new JTextField();
    private JLabel ipLabel = new JLabel("IP Address");
    private JLabel ipPortLabel = new JLabel("Port");
    private JButton goButton = new JButton("Go");
    private JPanel fieldsPanel = new JPanel();
    private String ipAddress;
    private String ipPort;
    private MainFrame frame = this;

    public MainFrame() {
        this.setTitle("Online TicTacToe");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(400,400));
        this.setVisible(true);
        this.pack();

        ipField.setPreferredSize(new Dimension(150,30));
        portField.setPreferredSize(new Dimension(40, 30));
        fieldsPanel.setLayout(new FlowLayout());
        fieldsPanel.add(ipLabel);
        fieldsPanel.add(ipField);
        fieldsPanel.add(ipPortLabel);
        fieldsPanel.add(portField);
        fieldsPanel.add(goButton);

        goButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ipAddress = ipField.getText();
                ipPort = portField.getText();
                try {
                    Socket socket = new Socket(ipAddress,Integer.parseInt(ipPort));
                    new GameFrame(socket,frame); // Si le client n'arrive pas à ouvrir une connexion avec le serveur, le jeu ne se lance pas.
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        this.add(fieldsPanel,BorderLayout.SOUTH);

        this.getContentPane().add(introPanel);

    }

    public static void main (String[] args){
        new MainFrame();
    }
}
