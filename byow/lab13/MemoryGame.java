package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

/**
 * A small Memory game.
 * This game is much like the electronic toy Simon,
 * but on a computer and with a keyboard instead of with 4 colored buttons.
 * The goal of the game will be to type in a randomly generated target string of characters
 * after it is briefly displayed on the screen one letter at a time.
 * The target string starts off as a single letter,
 * but for each successful string entered,
 * the game gets harder by making the target string longer.
 *
 * source: <a href="https://sp21.datastructur.es/materials/lab/lab13/lab13">...</a>
 */
public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        // long seed = Long.parseLong(args[0]);
        long seed = 38384;
        MemoryGame game = new MemoryGame(50, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.rand = new Random(seed);
    }

    /** Return the random string with the length N. */
    public String generateRandomString(int n) {
        final int lengthOfAlphabets = 26;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int idx = rand.nextInt(lengthOfAlphabets);
            sb.append(CHARACTERS[idx]);
        }
        return sb.toString();
    }

    /** Draw a frame with the text S to screen, and pause for pauseTime milliseconds. */
    public void drawFrame(String s, int pauseTime) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (!gameOver) {
            int y = height - 3;
            StdDraw.textLeft(0, y, "Round: " + round);
            StdDraw.textRight(width, y, ENCOURAGEMENT[rand.nextInt(7)]);
            String turn;
            if (playerTurn) {
                turn = "Type!";
            } else {
                turn = "Watch!";
            }
            StdDraw.text(width / 2, y, turn);
            StdDraw.line(0, height - 4, width, height - 4);
        }
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
        StdDraw.pause(pauseTime);
    }

    /** Draw a frame with the text S to screen, and pause for 100 milliseconds. */
    public void drawFrame(String s) {
        drawFrame(s, 100);
    }

    /** Draw the frame letter by letter. */
    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(Character.toString(letters.charAt(i)), 1000);
            drawFrame("", 500);
        }
    }

    /** Return the input string when the length is reached to N. */
    public String solicitNCharsInput(int n) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < n) {
            while (StdDraw.hasNextKeyTyped()) {
                sb.append(StdDraw.nextKeyTyped());
                drawFrame(sb.toString());
                i++;
            }
        }
        return sb.toString();
    }

    /** Start the game. */
    public void startGame() {
        round = 1;
        gameOver = false;
        while (!gameOver) {
            playerTurn = false;
            drawFrame("Round: " + round, 500);
            String randomStr = generateRandomString(round);
            flashSequence(randomStr);
            playerTurn = true;
            drawFrame("");
            String input = solicitNCharsInput(round);
            if (input.equals(randomStr)) {
                round++;
            } else {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + round);
            }
        }
    }
}
