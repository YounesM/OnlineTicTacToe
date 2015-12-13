import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Younes on 03/12/2015.
 */
public class GamePanel extends JPanel implements Runnable{
    /*
        JPanel où se déroule le jeu.
        (Organisation des constantes et mouseAdapter grandement inspirés par https://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe.html)
     */

    //Constentes
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int TILE_SIZE = 100;
    public static final int PANEL_WIDTH = COLS*TILE_SIZE;
    public static final int PANEL_HEIGHT = ROWS*TILE_SIZE;
    public static final int LINE_THICKNESS = 1;
    public static final int PADDING = 15;

    private Cell[][] board = new Cell[3][3];
    private boolean noWinner = true;
    private boolean isTurn = false;
    private GameStatus gameStatus;
    private GameFrame parent;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public GamePanel(GameFrame parent, Socket socket) {
        this.gameStatus = new GameStatus(noWinner,board,isTurn);
        this.socket=socket;
        this.parent=parent;
        this.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
        this.init();
        new Thread(this).start();
        this.addMouseListener(new MouseClick());
    }

    private class MouseClick extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            /*
                Lorsque le joueur click on récupère les coordonnées de le cellule, on en change son contenu s'il est
                nul est on envoie l'update au serveur.
             */
            int mouseX = e.getX();
            int mouseY = e.getY();

            int rowSelected = mouseX / TILE_SIZE;
            int colSelected = mouseY / TILE_SIZE;

            if(isTurn) {
                int flag=0;
                if (board[rowSelected][colSelected] == Cell.NULL) {
                    board[rowSelected][colSelected] = Cell.CROSS;
                    flag++;
                }
                if (flag!=0){
                    isTurn=false;
                }
                gameStatus.setBoard(board);
                gameStatus.setIsTurn(isTurn);
                repaint();
                check(); // On vérifie si le client a gagné avant de lancer l'update.
                if(!isTurn)
                    sendUpdate();
                if(!noWinner){
                    prompt(); //Affiche du message de victoire/défaite/égalité au client
                }
            }
        }
    }

    public void init(){
        /*
            Initialise la grille
         */
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j]=Cell.NULL;
            }
        }
    }

    public void sendUpdate(){
        /*
            Envoie l'update au serveur
         */
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(gameStatus);
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void check(){
        /*
            Vérifie si le client a gagné, perdu ou s'il y a égalité
         */
        if(board[0][0]==board[1][0] && board[1][0]==board[2][0] && board[2][0]==Cell.CIRCLE
                || board[0][1]==board[1][1] && board[1][1]==board[2][1] && board[2][1]==Cell.CIRCLE
                || board[0][2]==board[1][2] && board[1][2]==board[2][2] && board[2][2]==Cell.CIRCLE
                || board[0][0]==board[0][1] && board[0][1]==board[0][2] && board[0][2]==Cell.CIRCLE
                || board[1][0]==board[1][1] && board[1][1]==board[1][2] && board[1][2]==Cell.CIRCLE
                || board[2][0]==board[2][1] && board[2][1]==board[2][2] && board[2][2]==Cell.CIRCLE
                || board[0][0]==board[1][1] && board[1][1]==board[2][2] && board[2][2]==Cell.CIRCLE
                || board[2][0]==board[1][1] && board[1][1]==board[0][2] && board[0][2]==Cell.CIRCLE){
            noWinner=false;
            gameStatus.setGameRunning(noWinner);
        }
        if(board[0][0]==board[1][0] && board[1][0]==board[2][0] && board[2][0]==Cell.CROSS
                || board[0][1]==board[1][1] && board[1][1]==board[2][1] && board[2][1]==Cell.CROSS
                || board[0][2]==board[1][2] && board[1][2]==board[2][2] && board[2][2]==Cell.CROSS
                || board[0][0]==board[0][1] && board[0][1]==board[0][2] && board[0][2]==Cell.CROSS
                || board[1][0]==board[1][1] && board[1][1]==board[1][2] && board[1][2]==Cell.CROSS
                || board[2][0]==board[2][1] && board[2][1]==board[2][2] && board[2][2]==Cell.CROSS
                || board[0][0]==board[1][1] && board[1][1]==board[2][2] && board[2][2]==Cell.CROSS
                || board[2][0]==board[1][1] && board[1][1]==board[0][2] && board[0][2]==Cell.CROSS){
            noWinner=false;
            gameStatus.setGameRunning(noWinner);
        }
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j]==Cell.NULL)
                    count++;
            }
        }
        if(count==0 && noWinner){
            noWinner=false;
            gameStatus.setGameRunning(noWinner);
        }
    }

    public void prompt(){
        /*
            Affiche les messages en cas de victoire/parte/égalité
         */
        if(!noWinner &&(
                board[0][0]==board[1][0] && board[1][0]==board[2][0] && board[2][0]==Cell.CIRCLE
                || board[0][1]==board[1][1] && board[1][1]==board[2][1] && board[2][1]==Cell.CIRCLE
                || board[0][2]==board[1][2] && board[1][2]==board[2][2] && board[2][2]==Cell.CIRCLE
                || board[0][0]==board[0][1] && board[0][1]==board[0][2] && board[0][2]==Cell.CIRCLE
                || board[1][0]==board[1][1] && board[1][1]==board[1][2] && board[1][2]==Cell.CIRCLE
                || board[2][0]==board[2][1] && board[2][1]==board[2][2] && board[2][2]==Cell.CIRCLE
                || board[0][0]==board[1][1] && board[1][1]==board[2][2] && board[2][2]==Cell.CIRCLE
                || board[2][0]==board[1][1] && board[1][1]==board[0][2] && board[0][2]==Cell.CIRCLE)){
            JOptionPane.showMessageDialog(this,"Game over, you lost.","Looser",JOptionPane.INFORMATION_MESSAGE);
            parent.setVisible(false);
        }
        if(!noWinner
                && (board[0][0]==board[1][0] && board[1][0]==board[2][0] && board[2][0]==Cell.CROSS
                || board[0][1]==board[1][1] && board[1][1]==board[2][1] && board[2][1]==Cell.CROSS
                || board[0][2]==board[1][2] && board[1][2]==board[2][2] && board[2][2]==Cell.CROSS
                || board[0][0]==board[0][1] && board[0][1]==board[0][2] && board[0][2]==Cell.CROSS
                || board[1][0]==board[1][1] && board[1][1]==board[1][2] && board[1][2]==Cell.CROSS
                || board[2][0]==board[2][1] && board[2][1]==board[2][2] && board[2][2]==Cell.CROSS
                || board[0][0]==board[1][1] && board[1][1]==board[2][2] && board[2][2]==Cell.CROSS
                || board[2][0]==board[1][1] && board[1][1]==board[0][2] && board[0][2]==Cell.CROSS)){
            JOptionPane.showMessageDialog(this,"You win !","Winner",JOptionPane.INFORMATION_MESSAGE);
            parent.setVisible(false);
        }
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j]==Cell.NULL)
                    count++;
            }
        }
        if(count==0 && noWinner){
            JOptionPane.showMessageDialog(this,"Tie !","Tie",JOptionPane.INFORMATION_MESSAGE);
            parent.setVisible(false);
        }
    }

    @Override
    public void paintComponent(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0,0,PANEL_WIDTH,PANEL_HEIGHT);
        g.setColor(Color.GRAY);
        for (int i = 1; i < COLS ; i++) {
            for (int j = -LINE_THICKNESS ; j < LINE_THICKNESS ; j++) {
                g.drawLine(i*TILE_SIZE-j,PADDING,i*TILE_SIZE-j,PANEL_HEIGHT-PADDING);
            }
        }
        for (int i = 1; i < ROWS; i++) {
            for (int j = -LINE_THICKNESS; j < LINE_THICKNESS; j++) {
                g.drawLine(PADDING,i*TILE_SIZE-j,PANEL_WIDTH-PADDING,i*TILE_SIZE-j);
            }
        }
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if(board[i][j]==Cell.CROSS){
                    g.setColor(Color.red);
                    g.drawLine(i*TILE_SIZE+PADDING/2,j*TILE_SIZE+PADDING/2,i*TILE_SIZE+TILE_SIZE-PADDING/2,j*TILE_SIZE+TILE_SIZE-PADDING/2);
                    g.drawLine(i*TILE_SIZE+TILE_SIZE-PADDING/2,j*TILE_SIZE+PADDING/2,i*TILE_SIZE+PADDING/2,j*TILE_SIZE+TILE_SIZE-PADDING/2);
                }
                if (board[i][j]==Cell.CIRCLE) {
                    g.setColor(Color.blue);
                    g.drawOval(i*TILE_SIZE+PADDING/2,j*TILE_SIZE+PADDING/2,TILE_SIZE-PADDING,TILE_SIZE-PADDING);
                }
            }
        }
        if (!isTurn){
            Font f = new Font("Arial",2,25);
            g.setFont(f);
            g.drawString("Waiting for other player...",20,170);
        }
    }

    @Override
    public void run() {
        /*
            Réceptionne les entrées vers le serveur et mets à jour l'état du jeu
         */
        while(noWinner){
            this.repaint();
            try {
                in = new ObjectInputStream(socket.getInputStream());
                gameStatus = (GameStatus) in.readObject();
                board=gameStatus.getBoard();
                isTurn=gameStatus.isTurn();
                noWinner=gameStatus.isGameRunning();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            check();
            if(!noWinner){
                prompt();
            }
            this.repaint();
        }
    }
}
