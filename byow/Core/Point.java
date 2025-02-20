package byow.Core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a x,y coordinate system.
 */
public class Point implements Serializable {
    private final int x;
    private final int y;
    /**
     * The integer index of the point.
     */
    private final int iPoint;
    /**
     * Follows are the 4 directions.
     */
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int WEST = 2;
    public static final int EAST = 3;
    public static final int DIRECTION_INIT = 4;
    public static final int WIDTH_FACTOR = (int) Math.pow(10, String.valueOf(Engine.WIDTH).length());

    /**
     * Create an instance by given X and Y.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.iPoint = xyToIPoint(x, y);
    }

    /**
     * Create an instance by given iPoint.
     */
    public Point(int iPoint) {
        this(iPoint / WIDTH_FACTOR, iPoint % WIDTH_FACTOR);
    }

    /**
     * return the X
     */
    public int getX() {
        return this.x;
    }

    /**
     * return the Y
     */
    public int getY() {
        return this.y;
    }

    /**
     * return the iPoint
     */
    public int getIPoint() {
        return this.iPoint;
    }

    /**
     * Return the Vision points of the given point and the vision scope that defined in Game.class.
     */
    public static List<Integer> getVision(Point point, int visionScope) {
        List<Integer> pointList = new ArrayList<>();
        Point sw = point.getShiftPoint(-visionScope, -visionScope);
        int startX = sw.getX();
        int startY = sw.getY();
        for (int i = startX; i < startX + visionScope * 2 + 1; i++) {
            for (int j = startY; j < startY + visionScope * 2 + 1; j++) {
                if (Point.checkBound(i, j)) {
                    Point p = new Point(i, j);
                    pointList.add(p.getIPoint());
                }
            }
        }
        return pointList;
    }

    /**
     * return the direction of the given point.
     */
    public static int getDirection(Point point, Point prevPoint) {;
        if (point.getX() == prevPoint.getX()) {
            if (point.getY() > prevPoint.getY()) {
                return NORTH;
            } else {
                return SOUTH;
            }
        } else {
            if (point.getX() > prevPoint.getX()) {
                return EAST;
            } else {
                return WEST;
            }
        }
    }

    /**
     * Return the next point of the given point and the given direction.
     */
    public Point getNextPoint(int direction) {
        return switch (direction) {
            case NORTH -> getShiftPoint(0, 1);
            case SOUTH -> getShiftPoint(0, -1);
            case WEST -> getShiftPoint(-1, 0);
            case EAST -> getShiftPoint(1, 0);
            default -> throw new IndexOutOfBoundsException("No such direction.");
        };
    }

    /**
     * Return the neighbours of the given point by the sequence NSWE.
     */
    public static Point[] getNeighbours(Point point) {
        return new Point[]{point.getNextPoint(NORTH), point.getNextPoint(SOUTH),
                point.getNextPoint(WEST), point.getNextPoint(EAST)};
    }

    /**
     * return the point by shift value.
     */
    public Point getShiftPoint(int dx, int dy) {
        return new Point(this.x + dx, this.y + dy);
    }

    /**
     * return the index value (xy) of the point.
     */
    private int xyToIPoint(int x, int y) {
        return x * WIDTH_FACTOR + y;
    }

    /**
     * Check if the point is in the frame.
     */
    public static boolean checkBound(int x, int y) {
        return x >= 0 && x < Engine.WIDTH && y >= 0 && y <= Engine.HEIGHT;
    }

    /**
     * Check if the point is in the frame.
     */
    public static boolean checkBound(Point point) {
        return checkBound(point.getX(), point.getY());
    }

    @Override
    public String toString() {
        return String.valueOf(this.iPoint);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != Point.class) {
            return false;
        }
        return this.iPoint == ((Point) o).iPoint;
    }

    @Override
    public int hashCode() {
        return this.iPoint;
    }
}
