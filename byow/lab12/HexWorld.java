package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 * Author: Daniel Feng
 */
public class HexWorld {

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);
    private static final int MAX_COLUMN = 5;
    private static final int MIN_COLUMN = 3;

    /** Fills the given 2D array of tiles with NOTHING tiles. */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Draw random hexagons.
     * @param width the width of the frame.
     * @param height the height of the frame.
     * @param sideLength the side length of the random hexagon.
     */
    public static void drawWorld(int width, int height, int sideLength) {
        if (width < getMinWidth(sideLength)
                || height < getMinHeight(sideLength)
                || sideLength < 2) {
            return;
        }

        TERenderer ter = new TERenderer();
        ter.initialize(width, height);

        TETile[][] tiles = new TETile[width][height];
        fillWithNothing(tiles);

        int x1 = (width - sideLength) / 2 - sideLength + 1;
        int x2 = x1;
        int y = height - 1;
        drawHexagonCol(tiles, sideLength, x1, height - 1, MAX_COLUMN);
        for (int i = MAX_COLUMN - 1; i >= MIN_COLUMN; i--) {
            x1 -= (2 * sideLength - 1);
            x2 += (2 * sideLength - 1);
            y -= sideLength;
            drawHexagonCol(tiles, sideLength, x1, y, i);
            drawHexagonCol(tiles, sideLength, x2, y, i);
        }
        ter.renderFrame(tiles);
    }

    /** Draw random hexagons with a frame. */
    public static void drawWorld(int sideLength) {
        drawWorld(getMinWidth(sideLength), getMinHeight(sideLength), sideLength);
    }

    /** Draw random hexagons with a frame, and set the side length to 2. */
    public static void drawWorld() {
        drawWorld(getMinWidth(2), getMinHeight(2), 2);
    }

    /** Draw N hexagons in a column.
     *
     * @param tiles the frame.
     * @param sideLength the side length of every hexagon.
     * @param x the x coordinate of the start point of a column.
     * @param y the y coordinate of the start point of a hexagon.
     * @param n the number of hexagons.
     */
    private static void drawHexagonCol(TETile[][] tiles, int sideLength, int x, int y, int n) {
        for (int i = 0; i < n; i++) {
            TETile tile = randomTile();
            tile = TETile.colorVariant(tile, RANDOM.nextInt(100), RANDOM.nextInt(100),
                    RANDOM.nextInt(100), new Random(SEED));
            drawHexagon(tiles, tile, sideLength - 1, sideLength, x, y);
            y -= 2 * sideLength;
        }
    }

    /** Draw a hexagon.
     *
     * @param tiles the frame.
     * @param tile the tile with defined style.
     * @param nEmpty the number of empty tiles in first line of the hexagon.
     * @param nTiles the number of non-empty tiles in first line of the hexagon.
     * @param x the x coordinate of the start point of a hexagon.
     * @param y the y coordinate of the start point of a hexagon.
     */
    private static void drawHexagon(TETile[][] tiles, TETile tile, int nEmpty, int nTiles, int x, int y) {
        drawRow(tiles, tile, nEmpty, nTiles, x, y);
        if (nEmpty > 0) {
            drawHexagon(tiles, tile, nEmpty - 1, nTiles + 2, x, y - 1);
        }

        // draw refection row
        drawRow(tiles, tile, nEmpty, nTiles, x, y - 2 * nEmpty - 1);
    }

    /** Draw a line of hexagon.
     *
     * @param tiles the frame.
     * @param tile the tile with defined style.
     * @param nEmpty the number of empty tiles in the line.
     * @param nTiles the number of non-empty tiles in the line.
     * @param x the x coordinate of the start point of a hexagon.
     * @param y the x coordinate of the start point of a hexagon.
     */
    private static void drawRow(TETile[][] tiles, TETile tile, int nEmpty, int nTiles, int x, int y) {
        for (int i = 0; i < nTiles; i++) {
            tiles[nEmpty + x + i][y] = tile;
        }
    }

    /** Return a RANDOM tile. */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(11);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.FLOOR;
            case 4: return Tileset.AVATAR;
            case 5: return Tileset.GRASS;
            case 6: return Tileset.LOCKED_DOOR;
            case 7: return Tileset.MOUNTAIN;
            case 8: return Tileset.SAND;
            case 9: return Tileset.UNLOCKED_DOOR;
            case 10: return Tileset.WATER;
            default: throw new RuntimeException("Should not be here!");
        }
    }

    /** Return the minimal width requirement of the frame. */
    private static int getMinWidth(int sideLength) {
        return (2 * (MAX_COLUMN - MIN_COLUMN + 1)) * (2 * sideLength - 1);
    }

    /** Return the minimal height requirement of the frame. */
    private static int getMinHeight(int sideLength) {
        return MAX_COLUMN * sideLength * 2;
    }

    public static void main(String[] args) {
        drawWorld(4);
    }
}
