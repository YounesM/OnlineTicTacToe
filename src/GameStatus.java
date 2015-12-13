import java.io.Serializable;

/**
 * Created by Younes on 04/12/2015.
 */
public class GameStatus implements Serializable {
    /*
        Classe contenant l'état du jeu
     */
    private static final long serialVersionUID = 6227095513736517684L;
    private boolean gameRunning;
    private Cell[][] board;
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
