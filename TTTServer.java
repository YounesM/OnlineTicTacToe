import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.Serializable;

/**
 * Created by Younes on 04/12/2015.
 */

class GameStatus implements Serializable {
    /*
        Classe contenant l'état du jeu, que l'on échange avec les clients
     */
    private static final long serialVersionUID = 6227095513736517684L;
    private boolean gameRunning;
    private Cell[][] board ;
    private boolean isTurn;

    public GameStatus(boolean gameRunning, Cell[][] board, boolean isTurn) {
        this.gameRunning = gameRunning;
        this.board = board;
        this.isTurn = isTurn;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setIsTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
}

enum Cell{
    /*
        Enumération des états possibles des cellules
     */
    CROSS,CIRCLE,NULL
}

public class TTTServer {
    /*
        Code coté serveur : Pour qu'il n'y ai pas de surcharge, le serveur ne s'occupe que de transmètre l'état du jeu.
        Le calcul est réalisé par le client.
     */
    private Cell[][] board = new Cell[3][3];
    private boolean gameRunning;
    private GameStatus gameStatus = new GameStatus(true,board,true);
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public TTTServer() throws IOException,ClassNotFoundException{
        int port = Integer.parseInt(JOptionPane.showInputDialog("Connect to which port ?","7777"));
        ServerSocket serverSocket = new ServerSocket(port); // Ouverture d'un socket seveur
        while (true){
            /*
                On attends la connexion de deux joueurs avant de lancer le jeu.
             */
            System.out.println("Waiting for players...");
            Socket player1Socket = serverSocket.accept();
            System.out.println("Client connected. Waiting for one more participant.");
            Socket player2Socket = serverSocket.accept();
            System.out.println("Client connected.");
            System.out.println("Starting game");
            init(); //On initialise notre table de jeu.
            gameRunning = true;
            while (gameRunning){
                /*
                    On donne la main à tour de rôle aux joueurs. Tant que l'un des clients ne nous signale pas qu'il a gagné.
                    La variable gameStatus.isTurn empêche le joueur de jouer si ce n'est pas à lui de jouer.
                 */
                System.out.println("Player1's turn");
                out = new ObjectOutputStream(player1Socket.getOutputStream());
                out.writeObject(new GameStatus(gameRunning,board,true)); //On envoie de l'état du board enregistré par le serveur et on donne la main au joueur 1
                out.flush();
                System.out.println("Waiting for P1 to play...");
                in = new ObjectInputStream(player1Socket.getInputStream()); //On réceptionne les updates envoyées par le joueur 1
                gameStatus = (GameStatus) in.readObject();
                board = gameStatus.getBoard(); // On modifie les données côté serveur pour qu'elles correspondent à l'update
                gameRunning = gameStatus.isGameRunning();

                System.out.println("Received data from P1.");
                System.out.println("Inverting...");
                invert(); // On inverse le contenu de la table de jeu (X en O et O en X) de telle sorte que le client joue toujours X.
                System.out.println("Sending update to P2."); //On envoie les données du serveurs mises à jour au joueur 2 pour qu'il puisse à son tour les mettre à jour.
                out = new ObjectOutputStream(player2Socket.getOutputStream());
                out.writeObject(gameStatus);
                out.flush();
                if(!gameRunning){
                    System.out.println("Game Over");
                    break;
                }

                /*
                    On réittère les mêmes actions pour le tour de jeu du joueur 2
                 */

                System.out.println("Player2's turn");
                out = new ObjectOutputStream(player2Socket.getOutputStream());
                out.writeObject(new GameStatus(gameRunning,board,true));
                out.flush();
                System.out.println("Waiting for P2 to play...");
                in = new ObjectInputStream(player2Socket.getInputStream());
                gameStatus = (GameStatus) in.readObject();
                board = gameStatus.getBoard();
                gameRunning = gameStatus.isGameRunning();
                System.out.println("Is game running ? : "+gameRunning);

                System.out.println("Received data from P2.");
                System.out.println("Inverting...");
                invert();
                System.out.println("Sending update to P1.");
                out = new ObjectOutputStream(player1Socket.getOutputStream());
                out.writeObject(gameStatus);
                out.flush();
                if(!gameRunning){
                    System.out.println("Game Over.");
                    break;
                }
            }
            //Fermeture des sockets
            player1Socket.close();
            player2Socket.close();
        }
    }

    public void init(){
        /*
            Initialise la table de jeu
         */
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j]=Cell.NULL;
            }
        }
    }

    private void invert(){
        /*
            Inverse le contenu de la table de jeu
         */
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
                if(board[j][k]==Cell.CIRCLE){
                    board[j][k]=Cell.CROSS;
                }
                else if(board[j][k]==Cell.CROSS){
                    board[j][k]=Cell.CIRCLE;
                }
            }
        }
    }

    public static void main (String[] args) throws IOException, ClassNotFoundException {
        new TTTServer();
    }
}
