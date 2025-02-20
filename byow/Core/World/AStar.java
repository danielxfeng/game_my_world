package byow.Core.World;

import byow.Core.Point;
import byow.Core.TileBrick;
import byow.Core.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This class implement the A* algorithm for find the shortest path between 2 rooms.
 */
public class AStar {

    /**
     * The value of INF.
     */
    private final int INF = 99999;
    /**
     * The start room.
     */
    private final Room startRoom;
    /**
     * The destination room.
     */
    private final Room targetRoom;
    /**
     * An array of edges.
     */
    private final Edge[] edges;
    /**
     * The deque of vertices.
     */
    private final PriorityQueue<Edge> deque;

    /**
     * The array of tile bricks.
     */
    private final TileBrick[] tileBricks;

    /**
     * The start point.
     */
    private final int startPoint;
    /**
     * The hallwayMap of the frame.
     */
    private final Map<String, Hallway> hallwayMap;

    /**
     * This inner class respects a data structure of an Edge
     */
    private class Edge implements Comparable<Edge> {

        /**
         * The index of the edge.
         */
        private final int index;
        /**
         * The distance that have gone through.
         */
        private int distTo;
        /**
         * The Manhattan distance to the target.
         */
        private int h;
        /**
         * The priority value of the edge.
         */
        private int priority;
        /**
         * The previous edge.
         */
        private Edge prev;

        public Edge(int index, int h) {
            this(index, INF, h, null);
        }

        public Edge(int index, int distTo, int h, Edge prev) {
            this.index = index;
            this.distTo = distTo;
            this.h = h;
            this.priority = this.distTo + this.h;
            this.prev = prev;
        }

        @Override
        public int compareTo(Edge e) {
            return this.priority - e.priority;
        }

        @Override
        public String toString() {
            return "Edge: " + this.index + ", distTo: " + this.distTo
                    + ", h: " + this.h + ", priority: " + this.priority;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != Edge.class) {
                return false;
            }
            Edge e = (Edge) o;
            return this.index == e.index;
        }

        @Override
        public int hashCode() {
            return this.index;
        }
    }

    public AStar(Room room1, Room room2, TileBrick[] tileBricks, Map<String, Hallway> hallwayMap) {
        this.startRoom = room1;
        this.targetRoom = room2;
        this.hallwayMap = hallwayMap;
        this.startPoint = this.startRoom.getCentralPoint().getIPoint();
        this.tileBricks = tileBricks;
        int volume = Frame.VOLUME;
        this.edges = new Edge[volume];
        this.deque = new PriorityQueue<>();
        for (int i = 0; i < volume; i++) {
            Point p = new Point(i);
            this.edges[i] = new Edge(i, Utils.getDistance(p, targetRoom));
        }
    }

    /**
     * Return the shortest path from start point to target room.
     */
    public List<Point> runAPlus() {
        Edge startEdge = edges[startPoint];
        setEdgePriority(startEdge, 0, 0);
        List<Integer> vertices = startRoom.getPossibleGates();
        for (int intV : vertices) {
            Edge v = this.edges[intV];
            setEdgePrev(v, startEdge);
            deque.add(v);
        }
        while (!deque.isEmpty()) {
            Edge p = deque.poll();
            setEdgeDist(p, getEdgeDistTo(p.prev) + 1);
            Edge targetGate = getTargetGate(p);
            if (targetGate != null) {
                return getEdgePath(targetGate);
            }
            relax(p);
        }
        System.out.println("AStar.class: Cannot find a path between 2 rooms: "
                + startRoom.getCentralPoint() + " and " + targetRoom.getCentralPoint());
        return null;
    }

    /**
     * Relax the edge p.
     */
    private void relax(Edge p) {
        List<Integer> intQs = getVertices(p);
        int dist = getEdgeDistTo(p) + 1;
        for (int intQ : intQs) {
            Edge q = this.edges[intQ];
            if (updateEdgeDist(q, dist)) {
                deque.remove(q);
                setEdgePrev(q, p);
                deque.add(q);
            }
        }
    }

    /**
     * Return the possible vertices of this edge.
     */
    private List<Integer> getVertices(Edge edge) {
        Point[] neighbours = Point.getNeighbours(new Point(getEdgeIndex(edge)));
        ArrayList<Integer> vertices = new ArrayList<>();
        for (Point neighbour : neighbours) {
            int intNeighbour = neighbour.getIPoint();
            if (tileBricks[intNeighbour].getConstructionType() == TileBrick.CONSTRUCTION_TYPE_NOTHING) {
                vertices.add(intNeighbour);
            }
        }
        return vertices;
    }

    /**
     * Return connected point when 2 room can be connected. Otherwise, return null.
     */
    private Edge getTargetGate(Edge edge) {
        Point[] neighbours = Point.getNeighbours(new Point(getEdgeIndex(edge)));
        for (Point neighbour : neighbours) {
            if (isWallOrGate(neighbour)) {
                boolean isHallwayToTargetRoom = isHallwayToTargetRoom(neighbour);
                if (isHallwayToTargetRoom) {
                    hallwayMap.get(tileBricks[neighbour.getIPoint()].getKey()).addConnectedRoom(startRoom);
                }
                if (isTargetRoomButNotCorner(neighbour) || isHallwayToTargetRoom) {
                    Edge target = this.edges[neighbour.getIPoint()];
                    setEdgePrev(target, edge);
                    return target;
                }
            }
        }
        return null;
    }

    /**
     * Return true if the point is a wall of a gate.
     */
    private boolean isWallOrGate(Point point) {
        TileBrick tileBrick = tileBricks[point.getIPoint()];
        return tileBrick.getType() == Construction.WALLS || tileBrick.getType() == Construction.GATES;
    }

    /**
     * Return true if the brick and the point is belonged to the target room.
     */
    private boolean isTargetRoomButNotCorner(Point point) {
        TileBrick tileBrick = tileBricks[point.getIPoint()];
        return tileBrick.getKey().equals(targetRoom.getKey()) && !targetRoom.isCorner(point);
    }

    /**
     * Return true if the brick is a hallway to the target room.
     */
    private boolean isHallwayToTargetRoom(Point point) {
        TileBrick tileBrick = tileBricks[point.getIPoint()];
        return tileBrick.getConstructionType() == TileBrick.CONSTRUCTION_TYPE_HALLWAY
                && hallwayMap.get(tileBrick.getKey()).containsRoom(targetRoom);
    }

    /**
     * Return the index of an Edge.
     */
    public int getEdgeIndex(Edge edge) {
        return edge.index;
    }

    /* ----------- Below are the methods of the Edge. ------------------ */

    /**
     * Return the distTo of an Edge.
     */
    public int getEdgeDistTo(Edge edge) {
        return edge.distTo;
    }

    /**
     * Return the List of a path.
     */
    public List<Point> getEdgePath(Edge edge) {
        List<Integer> edgeList = new ArrayList<>();
        getEdgePath(edge, edgeList);
        if (edgeList.isEmpty()) {
            return null;
        }
        int size = edgeList.size();
        ArrayList<Point> points = new ArrayList<>();
        for (int i = size - 2; i >= 0; i--) { // Use - 2 for avoiding the central point of startRoom.
            points.add(new Point(edgeList.get(i)));
        }
        return points;
    }

    /**
     * A helper method of getPath to do the recursive call.
     */
    private void getEdgePath(Edge edge, List<Integer> list) {
        if (edge == null) {
            return;
        }
        list.add(edge.index);
        getEdgePath(edge.prev, list);
    }

    /**
     * Set the distTo of an edge.
     */
    public void setEdgeDist(Edge edge, int value) {
        edge.distTo = value;
        updateEdgePriority(edge);
    }

    /**
     * Set the prev an edge.
     */
    private void setEdgePrev(Edge edge, Edge prev) {
        edge.prev = prev;
    }

    /**
     * Try to update the distTo of an edge.
     */
    public boolean updateEdgeDist(Edge edge, int value) {
        if (value < edge.distTo) {
            setEdgeDist(edge, value);
            return true;
        }
        return false;
    }

    /**
     * Update the priority of an edge.
     */
    public void updateEdgePriority(Edge edge) {
        edge.priority = edge.distTo + edge.h;
    }

    /**
     * Set the priority of an edge by given distTo and h.
     */
    public void setEdgePriority(Edge edge, int distTo, int h) {
        edge.distTo = distTo;
        edge.h = h;
        updateEdgePriority(edge);
    }
}
