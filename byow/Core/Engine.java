package byow.Core;

import byow.Core.InputPackage.InputMethods;
import byow.Core.InputPackage.KeyInput;
import byow.Core.InputPackage.StrInput;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.Serializable;

/**
 * This class is to respond the user command.
 */
public class Engine implements Serializable {

    /**
     * The width of the frame.
     */
    public static final int WIDTH = 80;
    /**
     * The height of the frame.
     */
    public static final int HEIGHT = 30;
    /**
     * The top space for showing the tips of the frame.
     */
    public static final int INFO_HEIGHT = 5;
    /**
     * The pause time for showing a frame.
     */
    private final int PAUSE_TIME = 500;
    /**
     * The vision scope of the player.
     */
    public static final int VISION_SCOPE = 3;
    /**
     * The main instance of the game.
     */
    private Game game;

    /**
     * Parse the input.
     */
    private void parseInput(InputMethods input) {
        final int stage1 = 0;
        final int stage2 = 1;
        boolean isFirstCommand = true;
        boolean waitingForSeed = false;
        boolean waitingForSaveQ = false;
        int stage = stage1;
        StringBuilder sb = new StringBuilder();
        while (input.possibleNextInput()) {
            char c = input.getNextKey();
            if (waitingForSaveQ && c != 'q') {
                waitingForSaveQ = false;
            }
            switch (c) {
                case 'n' -> {
                    if (isFirstCommand) {
                        waitingForSeed = true;
                        if (input.getClass() == KeyInput.class) {
                            showPrompt();
                        }
                    }
                }
                case 'l' -> {
                    if (isFirstCommand) {
                        stage = stage2;
                        this.game = Game.readFromFile();
                    }
                }
                case 'q' -> {
                    if (isFirstCommand) {
                        System.exit(0);
                    } else if (waitingForSaveQ) {
                        game.saveToFile();
                        System.exit(0);
                    }
                }
                case ':' -> {
                    if (stage == stage2) {
                        waitingForSaveQ = true;
                    }
                }
                case 's' -> {
                    if (stage == stage1 && waitingForSeed && sb.length() > 0) {
                        waitingForSeed = false;
                        stage = stage2;
                        this.game = new Game(Long.parseLong(sb.toString()));
                        sb = new StringBuilder();
                        game.newWorld();
                        game.interactiveGame();
                    } else if (stage == stage2) {
                        game.move(c);
                    }
                }
                case 'v' -> {
                    if (stage == stage2) {
                        if (game.getVisionScope() == 0) {
                            game.setVisionScope(VISION_SCOPE);
                        } else {
                            game.setVisionScope(0);
                        }
                        game.fillAllTiles(true);
                        game.render();
                    }
                }
                default -> {
                    if (stage == stage2 && (c == 'w' || c == 'a' || c == 'd')) {
                        game.move(c);
                    } else if (waitingForSeed && Character.isDigit(c)) {
                        sb.append(c);
                        if (input.getClass().equals(KeyInput.class)) {
                            showSeed(sb.toString());
                        }
                    }
                }
            }
            isFirstCommand = false;
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        KeyInput keyInput = new KeyInput();
        showMainMenu();
        parseInput(keyInput);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public void interactWithInputString(String input) {
        StrInput strInput = new StrInput(input);
        parseInput(strInput);
    }

    /**
     * Show main menu.
     */
    private void showMainMenu() {
        final int fontSize = 30;
        final int canvasFactor = 16;
        final double titleHeight = 4 / 3.0;
        final double menuHeight = 2.0;
        final double menuGap = 2.5;
        StdDraw.setCanvasSize(Engine.WIDTH * canvasFactor,
                (Engine.HEIGHT + Engine.INFO_HEIGHT) * canvasFactor);
        Font font = new Font("Monaco", Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, Engine.WIDTH);
        StdDraw.setYscale(0, Engine.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text((double) Engine.WIDTH / 2,
                Engine.HEIGHT / titleHeight,
                "CS61B: THE GAME");
        StdDraw.text((double) Engine.WIDTH / 2,
                (double) Engine.HEIGHT / menuHeight,
                "New Game (N)");
        StdDraw.text((double) Engine.WIDTH / 2,
                (double) Engine.HEIGHT / menuHeight - menuGap,
                "Load Game (L)");
        StdDraw.text((double) Engine.WIDTH / 2,
                (double) Engine.HEIGHT / menuHeight - menuGap * 2,
                "Quit (Q)");
        StdDraw.show();
        StdDraw.pause(PAUSE_TIME);
    }

    /**
     * Show the prompt when player choose new game.
     */
    private void showPrompt() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2,
                "Please input numbers to generate a world: ");
        StdDraw.show();
        StdDraw.pause(PAUSE_TIME);
    }

    /**
     * Show the inout seed when player input.
     */
    private void showSeed(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2, "Seed: " + seed);
        StdDraw.show();
        StdDraw.pause(PAUSE_TIME);
    }
}
