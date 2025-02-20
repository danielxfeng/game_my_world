package byow.Core;

import byow.Core.World.Construction;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

/**
 * This class respects a TileBrick of a frame.
 */
public class TileBrick implements Serializable {
    private int type;
    private int constructionType;
    private String constructionKey;
    private TileBrick hideOne;
    public static final int CONSTRUCTION_TYPE_ROOM = 0;
    public static final int CONSTRUCTION_TYPE_HALLWAY = 1;
    public static final int CONSTRUCTION_TYPE_NOTHING = 2;

    public TileBrick() {
        this.type = Construction.NOTHING;
        this.constructionType = CONSTRUCTION_TYPE_NOTHING;
        this.constructionKey = "None";
        this.hideOne = null;
    }

    /**
     * Return the constructionKey of a tile brick.
     */
    public String getKey() {
        return this.constructionKey;
    }

    /**
     * Return the constructionType of a tile brick.
     */
    public int getConstructionType() {
        return this.constructionType;
    }

    /**
     * Return the constructionTypeString of a tile brick.
     */
    public String getConstructionTypeString() {
        return switch (this.constructionType) {
            case CONSTRUCTION_TYPE_ROOM -> "Room";
            case CONSTRUCTION_TYPE_HALLWAY -> "Hallway";
            default -> "Empty";
        };
    }

    /**
     * Return the type of a tile brick.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Return the hideOne of a tile brick.
     */
    public String getTypeString() {
        return switch (this.type) {
            case Construction.WALLS -> "Wall";
            case Construction.BRICKS -> "Brick";
            case Construction.GATES -> "Locked Gate";
            case Construction.UNLOCKED_GATES -> "Unlocked Gate";
            case Game.PLAYER -> getHideOne().getTypeString();
            default -> "Nothing";
        };
    }

    /**
     * Return the style of a tile brick.
     */
    public TETile getStyle() {
        return switch (this.getType()) {
            case Construction.WALLS -> Construction.WALL_TILE;
            case Construction.BRICKS -> Construction.BRICK_TILE;
            case Construction.GATES -> Construction.GATE_TILE;
            case Construction.UNLOCKED_GATES -> Construction.UNLOCKED_GATE_TILE;
            case Game.PLAYER -> Game.PLAYER_TILE;
            default -> Tileset.NOTHING;
        };
    }

    /**
     * Return the hideOne when the player had moved.
     */
    public TileBrick getHideOne() {
        return this.hideOne;
    }

    /**
     * Set the type and style of a tile brick.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Set the value of a tile brick.
     */
    public void setValue(int inType, int inConstructionType, String inConstructionKey) {
        this.type = inType;
        this.constructionType = inConstructionType;
        this.constructionKey = inConstructionKey;
    }

    /**
     * Return a new player brick, add the given one to its hideOne.
     */
    public static TileBrick setPlayer(TileBrick tileBrick) {
        TileBrick tb = new TileBrick();
        tb.setValue(Game.PLAYER, tileBrick.constructionType, tileBrick.constructionKey);
        tb.hideOne = tileBrick;
        return tb;
    }
}
