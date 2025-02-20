package byow.Core;

import byow.Core.World.Construction;
import byow.Core.World.Frame;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is used to represent the game.
 */
public class Game implements Serializable {

    /**
     * The file for serialize and save the instance to disk.
     */
    private static final File OBJ_FILE = Utils.join(new File(System.getProperty("user.dir")), "my_world.obj");
    /**
     * The code of player.
     */
    public static final int PLAYER = 100;
    /**
     * The style of player.
     */
    public static final TETile PLAYER_TILE = Tileset.AVATAR;
    /**
     * The position of the player.
     */
    private Point iPointOfPlayer;
    /**
     * The scope of player version, set to 0 for infinity.
     */
    private int visionScope;
    /**
     * Save the tileBrick of every tile.
     */
    private final TileBrick[] tileBricks;
    /**
     * The frame is filled by Tiles.
     */
    private transient TETile[][] tiles;
    /**
     * The renderer is used to render the frame.
     */
    transient TERenderer ter;
    /**
     * The random of the game.
     */
    private final transient Random rand;

    public Game(long seed) {
        this.rand = new Random(seed);
        this.tileBricks = new TileBrick[Frame.VOLUME];
        iPointOfPlayer = null;
        this.visionScope = Engine.VISION_SCOPE;
        for (int i = 0; i < Frame.VOLUME; i++) {
            this.tileBricks[i] = new TileBrick();
        }
        init();
    }

    /**
     * Return the vision scope.
     */
    public int getVisionScope() {
        return visionScope;
    }

    /**
     * Return the vision scope.
     */
    public void setVisionScope(int visionScope) {
        this.visionScope = visionScope;
    }

    /**
     * Initialise some fields when create and recovery.
     */
    private void init() {
        tiles = new TETile[Engine.WIDTH][Engine.HEIGHT];
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
    }

    /**
     * Start a new game with render a frame.
     */
    public void newWorld() {
        Frame frame = new Frame(rand, tileBricks);
        frame.create();
        fillAllTiles();
        render();
    }

    /**
     * Read the saved instance variables.
     */
    public static Game readFromFile() {
        Game game = Utils.readObject(OBJ_FILE, Game.class);
        game.init();
        game.fillAllTiles(true);
        game.render();
        return game;
    }

    /**
     * Fill a tile.
     */
    private void fillATile(int x, int y, boolean isHide) {
        if (isHide) {
            tiles[x][y] = Tileset.NOTHING;
        } else {
            Point point = new Point(x, y);
            tiles[x][y] = tileBricks[point.getIPoint()].getStyle();
        }
    }

    /**
     * Fill all the tiles with the limit of the vision scope.
     */
    public void fillAllTiles(Point point, boolean isHide) {
        System.out.println("iPoint: " + point);
        List<Integer> pointList = new ArrayList<>();
        if (point != null) {
            pointList = Point.getVision(point, this.visionScope);
        }
        boolean tileIsHide;
        for (int x = 0; x < Engine.WIDTH; x++) {
            for (int y = 0; y < Engine.HEIGHT; y++) {
                Point neighbour = new Point(x, y);
                tileIsHide = isHide && (visionScope > 0) && (!pointList.contains(neighbour.getIPoint())) ?
                        true : false;
                fillATile(x, y, tileIsHide);
            }
        }
    }

    /**
     * Fill all the tiles without limit the vision scope.
     */
    public void fillAllTiles() {
        fillAllTiles(null, false);
    }

    /**
     * Fill all the tiles without limit the vision scope.
     */
    public void fillAllTiles(boolean isHide) {
        fillAllTiles(this.iPointOfPlayer, isHide);
    }

    /**
     * Render the tiles.
     */
    private void render(String info) {
        ter.renderFrame(tiles, info);
    }

    /**
     * Render the tiles.
     */
    public void render() {
        String info = "";
        if (iPointOfPlayer != null) {
            TileBrick player = tileBricks[iPointOfPlayer.getIPoint()];
            info = "   I am at " + iPointOfPlayer + ", "
                    + "a " + player.getTypeString()
                    + " of a " + player.getConstructionTypeString() + ". "
                    + "Tip: Press 'v' to switch the vision scope, press ':q' to save and quit.";
        }
        render(info);
    }

    /**
     * Save the instance variables to disk.
     */
    public void saveToFile() {
        Utils.writeObject(OBJ_FILE, this);
    }

    /**
     * Start to interactiveGame.
     */
    public void interactiveGame() {
        int randomPosition = this.rand.nextInt(Frame.VOLUME);
        int iPoint = -1;
        while (randomPosition >= 0) {
            if (tileBricks[randomPosition].getType() == Construction.BRICKS) {
                iPoint = randomPosition;
                break;
            }
            randomPosition--;
        }
        while (randomPosition < Frame.VOLUME) {
            if (tileBricks[randomPosition].getType() == Construction.BRICKS) {
                iPoint = randomPosition;
                break;
            }
            randomPosition++;
        }
        setPlayer(new Point(iPoint));
    }

    /**
     * Set the player to a position.
     */
    private void setPlayer(Point point) {
        if (point.getIPoint() < 0 || point.getIPoint() >= Frame.VOLUME || !Point.checkBound(point)) {
            return;
        }
        iPointOfPlayer = point;
        tileBricks[point.getIPoint()] = TileBrick.setPlayer(tileBricks[point.getIPoint()]);
        fillAllTiles(point, true);
        render();
    }

    /**
     * Move the player and render the frame.
     */
    public void move(char c) {
        switch (c) {
            case 'w' -> move(Point.NORTH);
            case 's' -> move(Point.SOUTH);
            case 'a' -> move(Point.WEST);
            case 'd' -> move(Point.EAST);
            default -> throw new IllegalArgumentException("Invalid direction.");
        }
    }

    /**
     * Move the player and render the frame.
     */
    private void move(int direction) {
        Point nextPoint = iPointOfPlayer.getNextPoint(direction);
        int type = tileBricks[nextPoint.getIPoint()].getType();
        if (type == Construction.GATES) { // unlock the gate
            tileBricks[nextPoint.getIPoint()].setType(Construction.UNLOCKED_GATES);
            fillAllTiles(nextPoint, true);
            render();
        } else if (type == Construction.BRICKS || type == Construction.UNLOCKED_GATES) { // move to the next point
            tileBricks[iPointOfPlayer.getIPoint()] = tileBricks[iPointOfPlayer.getIPoint()].getHideOne();
            setPlayer(nextPoint);
        }
    }
}
