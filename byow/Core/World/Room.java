package byow.Core.World;

import byow.Core.Engine;
import byow.Core.Point;
import byow.Core.TileBrick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents a room.
 * The room is a square.
 */
public class Room extends Construction {

    /**
     * The point of southwest corner.
     */
    private final Point sw;
    /**
     * The width of the room.
     */
    private final int width;
    /**
     * The height of the room.
     */
    private final int height;
    /**
     * The max side length of the room.
     */
    private static final int MAX_SIDE_LENGTH = 10;
    /**
     * The min side length of the room.
     */
    private static final int MIN_SIDE_LENGTH = 4;

    /**
     * Create a room by a given random generator and lBound which is the bound of width and height.
     */
    public Room(Random rand, TileBrick[] tileBricks) {
        super(tileBricks);
        this.width = Room.getRandomLength(rand);
        this.height = Room.getRandomLength(rand);
        this.sw = new Point(rand.nextInt(Engine.WIDTH - width),
                rand.nextInt(Engine.HEIGHT - height));
        this.central = new Point(this.getSw().getX() + (this.width / 2),
                this.sw.getY() + (this.height / 2));
    }

    /**
     * Return a side length by a given random generator.
     */
    private static int getRandomLength(Random rand) {
        return rand.nextInt(MAX_SIDE_LENGTH - MIN_SIDE_LENGTH) + MIN_SIDE_LENGTH;
    }

    /**
     * Return the central point of the room.
     */
    public Point getCentralPoint() {
        return this.central;
    }

    /**
     * Return the width of the room.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Return the width of the room.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Return the Point of the Southwest corner of the room.
     */
    public Point getSw() {
        return this.sw;
    }

    /**
     * Return the Point of the Northwest corner of the room.
     */
    public Point getNw() {
        return this.sw.getShiftPoint(0, height - 1);
    }

    /**
     * Return the Point of the Southeast corner of the room.
     */
    public Point getSe() {
        return this.sw.getShiftPoint(width - 1, 0);
    }

    /**
     * Return the Point of the Northeast corner of the room.
     */
    public Point getNe() {
        return this.sw.getShiftPoint(width - 1, height - 1);
    }

    /**
     * Return the int index of the Southwest corner of the room.
     */
    public int getSwIndex() {
        return getSw().getIPoint();
    }

    /**
     * Return a list of possible gates which means the bricks of walls but not the corner.
     */
    public List<Integer> getPossibleGates() {
        List<Integer> possibleGates = new ArrayList<>();
        for (Point wallBrick : this.getWalls()) {
            if (!isCorner(wallBrick)) {
                possibleGates.add(wallBrick.getIPoint());
            }
        }
        return possibleGates;
    }

    /**
     * Return if a point is a corner of a room.
     */
    public boolean isCorner(Point point) {
        return point.equals(getSw())
                || point.equals(getNw())
                || point.equals(getSe())
                || point.equals(getNe());
    }

    /**
     * Create a room by the fields of the class
     */
    public boolean generateNewRoom() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
                    walls.add(sw.getShiftPoint(i, j));
                } else {
                    bricks.add(sw.getShiftPoint(i, j));
                }
            }
        }
        if (!checkConflict()) {
            return false;
        }
        insertToFrameFields();
        return true;
    }

    /**
     * Return true the room is NO conflict with other rooms.
     */
    private boolean checkConflict() {
        for (Point brick : bricks) {
            if (!checkConflict(brick)) {
                return false;
            }
        }
        for (Point wall : walls) {
            if (!checkConflict(wall)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the point is a part of other rooms.
     */
    private boolean checkConflict(Point p) {
        return tileBricks[p.getIPoint()].getConstructionType() == TileBrick.CONSTRUCTION_TYPE_NOTHING;
    }

    /**
     * Set the point of gate by the given point.
     */
    public boolean setGate(Point p) {
        if (this.isCorner(p)) {
            System.out.println("Tried to set a gate on a corner.");
            return false;
        }
        if (this.gates.contains(p)) {
            return true;
        }
        this.gates.add(p);
        this.walls.remove(p);
        tileBricks[p.getIPoint()].setValue(Construction.GATES, TileBrick.CONSTRUCTION_TYPE_ROOM, getKey());
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != Room.class) {
            return false;
        }
        return this.getKey().equals(((Room) o).getKey());
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }
}
