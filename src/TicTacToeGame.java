import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


class GameManager {
    private static GameManager instance;
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;

    private GameManager(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
    }

    public static GameManager getInstance(Board board, List<Player> players) {
        if (instance == null) {
            instance = new GameManager(board, players);
        }
        return instance;
    }

    public void startGame() {
        while (!board.isFull()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("Player " + currentPlayer.getNumber() + "'s Turn (" + currentPlayer.getSymbol() + ")");
            currentPlayer.makeMove(board);
            board.printBoard();

            if (board.checkWinner(currentPlayer.getSymbol())) {
                System.out.println("Player " + currentPlayer.getNumber() + " wins!");
                return;
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        System.out.println("It's a draw!");
    }
}

// Board - Represents the game board
class Board {
    private final int size;
    private final String[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = " ";
            }
        }
    }

    public int getSize() {
        return size;
    }

    public boolean isFull() {
        for (String[] row : grid) {
            for (String cell : row) {
                if (cell.equals(" ")) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size && grid[row][col].equals(" ");
    }

    public void updateBoard(int row, int col, String symbol) {
        grid[row][col] = symbol;
    }

    public boolean checkWinner(String symbol) {
        return checkRows(symbol) || checkColumns(symbol) || checkDiagonals(symbol);
    }

    private boolean checkRows(String symbol) {
        for (int i = 0; i < size; i++) {
            boolean win = true;
            for (int j = 0; j < size; j++) {
                if (!grid[i][j].equals(symbol)) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }
        return false;
    }

    private boolean checkColumns(String symbol) {
        for (int j = 0; j < size; j++) {
            boolean win = true;
            for (int i = 0; i < size; i++) {
                if (!grid[i][j].equals(symbol)) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }
        return false;
    }

    private boolean checkDiagonals(String symbol) {
        boolean mainDiagonal = true, antiDiagonal = true;
        for (int i = 0; i < size; i++) {
            if (!grid[i][i].equals(symbol)) mainDiagonal = false;
            if (!grid[i][size - i - 1].equals(symbol)) antiDiagonal = false;
        }
        return mainDiagonal || antiDiagonal;
    }

    public void printBoard() {
        for (String[] row : grid) {
            for (String cell : row) {
                System.out.print("| " + cell + " ");
            }
            System.out.println("|");
        }
    }
}
// Player Factory - Factory Pattern
class PlayerFactory {
    public static Player createPlayer(int number, String symbol, boolean isBot) {
        if (isBot) {
            return new BotPlayer(number, symbol, new RandomBotStrategy());
        } else {
            return new HumanPlayer(number, symbol, new HumanStrategy());
        }
    }
}

// Player - Abstract Class
abstract class Player {
    protected int number;
    protected String symbol;
    protected MoveStrategy strategy;

    public Player(int number, String symbol, MoveStrategy strategy) {
        this.number = number;
        this.symbol = symbol;
        this.strategy = strategy;
    }

    public int getNumber() {
        return number;
    }

    public String getSymbol() {
        return symbol;
    }

    public void makeMove(Board board) {
        strategy.makeMove(board, this.symbol);
    }
}


class HumanPlayer extends Player {
    public HumanPlayer(int number, String symbol, MoveStrategy strategy) {
        super(number, symbol, strategy);
    }
}


class BotPlayer extends Player {
    public BotPlayer(int number, String symbol, MoveStrategy strategy) {
        super(number, symbol, strategy);
    }
}


interface MoveStrategy {
    void makeMove(Board board, String symbol);
}

// HumanStrategy - Handles Human Input
class HumanStrategy implements MoveStrategy {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void makeMove(Board board, String symbol) {
        int row, col;
        while (true) {
            System.out.print("Enter row and column (e.g., 1 1): ");
            row = scanner.nextInt() - 1;
            col = scanner.nextInt() - 1;
            if (board.isValidMove(row, col)) {
                board.updateBoard(row, col, symbol);
                break;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }
}

// RandomBotStrategy - Bot Makes Random Moves
class RandomBotStrategy implements MoveStrategy {
    private final Random random = new Random();

    @Override
    public void makeMove(Board board, String symbol) {
        int row, col;
        while (true) {
            row = random.nextInt(board.getSize());
            col = random.nextInt(board.getSize());
            if (board.isValidMove(row, col)) {
                board.updateBoard(row, col, symbol);
                break;
            }
        }
        System.out.println("Bot chose: " + (row + 1) + " " + (col + 1));
    }
}

// Main Class
public class TicTacToeGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input Board Size
        System.out.print("Enter board size (n): ");
        int n = scanner.nextInt();

        // Input Number of Players
        System.out.print("Enter number of players (n-1): ");
        int playerCount = scanner.nextInt();

        // Input Number of Bots
        System.out.print("Enter number of bots: ");
        int botCount = scanner.nextInt();

        // Input Player Symbols
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            System.out.print("Enter symbol for Player " + i + ": ");
            String symbol = scanner.next();
            boolean isBot = i <= botCount;
            players.add(PlayerFactory.createPlayer(i, symbol, isBot));
        }

        // Initialize Board
        Board board = new Board(n);

        // Start Game
        GameManager gameManager = GameManager.getInstance(board, players);
        gameManager.startGame();
    }
}
