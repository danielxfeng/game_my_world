package byow.Core.World;

import byow.Core.Point;
import byow.Core.TileBrick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a hallway that implement the A* algorithm for finding the way.
 */
public class Hallway extends Construction {

    /**
     * The room that the hallway starts from.
     */
    private final Room startRoom;
    /**
     * The room that the hallway ends at.
     */
    private final Room targetRoom;
    /**
     * The connected rooms.
     */
    private final List<String> connectedRooms;
    /**
     * The hallwayMap of the frame.
     */
    private final Map<String, Hallway> hallwayMap;

    /**
     * Create a hallway by a vertex.
     */
    public Hallway(Room[] vertex, TileBrick[] properties, Map<String, Hallway> hallwayMap) {
        super(properties);
        this.hallwayMap = hallwayMap;
        Room room1 = vertex[0];
        Room room2 = vertex[1];
        if (room1.getSwIndex() + room1.getNe().getIPoint() < room2.getSwIndex() + room2.getNe().getIPoint()) {
            this.startRoom = room1;
            this.targetRoom = room2;
        } else {
            this.startRoom = room2;
            this.targetRoom = room1;
        }
        this.connectedRooms = new ArrayList<>();
        buildHallway();
    }

    /**
     * Build the hallway by the A* algorithm.
     */
    private void buildHallway() {
        AStar aStar = new AStar(startRoom, targetRoom, tileBricks, hallwayMap);
        List<Point> path = aStar.runAPlus();
        if (path == null) {
            return;
        }
        addPathToHallway(path);
        insertToFrameFields();
        addConnectedRoom(startRoom);
        addConnectedRoom(targetRoom);
    }

    /**
     * Add the path to the hallway.
     */
    private void addPathToHallway(List<Point> path) {
        int lastDirection = Point.DIRECTION_INIT;
        for (int i = 0; i < path.size(); i++) {
            Point point = path.get(i);
            if (i == 0) {
                boolean isSetGate = startRoom.setGate(point);
                if (!isSetGate) {
                    throw new RuntimeException("Cannot set gate for the start room.");
                }
            } else if (i == path.size() - 1) {
                boolean isSetGate = targetRoom.setGate(point);
                if (!isSetGate) {
                    bricks.add(point);
                }
            } else {
                bricks.add(point);
                Point prev = path.get(i - 1);
                int direction = Point.getDirection(point, prev);
                addHallwayWalls(path, point, prev, direction, lastDirection);
                if (direction != lastDirection) {
                    lastDirection = direction;
                }
            }
        }
    }

    /**
     * Add the walls of the hallway by a given point and its prev.
     */
    private void addHallwayWalls(List<Point> path, Point point, Point prev, int direction, int lastDirection) {
        Point[] neighbors = Point.getNeighbours(point);
        for (Point neighbor : neighbors) {
            addHallwayWalls(path, neighbor);
        }
        if (direction != lastDirection && lastDirection != Point.DIRECTION_INIT) {
            Point turn;
            if ((lastDirection == Point.NORTH && direction == Point.EAST)
                    || (lastDirection == Point.WEST && direction == Point.SOUTH)) {
                turn = prev.getShiftPoint(-1, 1); // add NorthWest corner;
            } else if (lastDirection == Point.SOUTH && direction == Point.EAST
                    || (lastDirection == Point.WEST && direction == Point.NORTH)) {
                turn = prev.getShiftPoint(-1, -1); // add SouthWest corner;
            } else if (lastDirection == Point.NORTH && direction == Point.WEST
                    || (lastDirection == Point.EAST && direction == Point.SOUTH)) {
                turn = prev.getShiftPoint(1, 1); // add NorthEast corner;
            } else {
                turn = prev.getShiftPoint(1, -1); // add SouthEast corner;
            }
            addHallwayWalls(path, turn);
        }
    }

    /**
     * Add the walls of the hallway by a given point after checking.
     */
    private void addHallwayWalls(List<Point> path, Point point) {
        if (!path.contains(point)
                && tileBricks[point.getIPoint()].getConstructionType() == TileBrick.CONSTRUCTION_TYPE_NOTHING) {
            walls.add(point);
        }
    }

    /**
     * Add a room to connected list.
     */
    public void addConnectedRoom(Room room) {
        connectedRooms.add(room.getKey());
    }

    /**
     * Check if the connectedRoom contains a room.
     */
    public boolean containsRoom(Room room) {
        return connectedRooms.contains(room.getKey());
    }
}
