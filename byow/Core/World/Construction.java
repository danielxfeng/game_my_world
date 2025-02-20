package byow.Core.World;

import byow.Core.Point;
import byow.Core.TileBrick;
import byow.Core.Utils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a construction in a frame such as a room or a hallway.
 */
public abstract class Construction {
    public static final int WALLS = 0;
    public static final int BRICKS = 1;
    public static final int GATES = 2;
    public static final int UNLOCKED_GATES = 3;
    public static final int NOTHING = 4;
    public static final TETile WALL_TILE = Tileset.WALL;
    public static final TETile BRICK_TILE = Tileset.FLOOR;
    public static final TETile GATE_TILE = Tileset.LOCKED_DOOR;
    public static final TETile UNLOCKED_GATE_TILE = Tileset.UNLOCKED_DOOR;
    /**
     * The name of the construction.
     */
    protected final String key;
    /**
     * The points of walls.
     */
    protected List<Point> walls;
    /**
     * The points of bricks which also means the interior space of a room.
     */
    protected List<Point> bricks;
    /**
     * The points of gates.
     */
    protected List<Point> gates;
    /**
     * The properties of the frame.
     */
    protected TileBrick[] tileBricks;
    protected Point central;

    protected Construction(TileBrick[] tileBricks) {
        this.tileBricks = tileBricks;
        this.key = Utils.getRandomUUID();
        this.bricks = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.gates = new ArrayList<>();
    }

    /**
     * Return the key of a room.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Return a List of wall points.
     */
    public List<Point> getWalls() {
        return this.walls;
    }

    /**
     * Return a List of brick points.
     */
    public List<Point> getBricks() {
        return this.bricks;
    }

    /**
     * Insert the construction to the tileBricks.
     */
    protected void insertToFrameFields () {
        int constructionType = this.getClass() == Room.class ?
                TileBrick.CONSTRUCTION_TYPE_ROOM : TileBrick.CONSTRUCTION_TYPE_HALLWAY;
        for (Point brick : bricks) {
            tileBricks[brick.getIPoint()].setValue(Construction.BRICKS, constructionType, getKey());
        }
        for (Point wall : walls) {
            tileBricks[wall.getIPoint()].setValue(Construction.WALLS, constructionType, getKey());
        }
    }
}
