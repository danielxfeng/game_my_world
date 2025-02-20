package byow.Core.World;

import byow.Core.Engine;
import byow.Core.Point;
import byow.Core.TileBrick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * This class respects the frame.
 */
public class Frame {

    /**
     * Save the tileBrick of every tile.
     */
    private final TileBrick[] tileBricks;
    /**
     * The rooms in the frame.
     */
    private final ArrayList<Room> rooms;
    /**
     * The hallways in the frame.
     */
    private final TreeMap<String, Hallway> hallways;
    /**
     * The random.
     */
    private final Random rand;
    /**
     * The volume of the DJS.
     */
    public static final int VOLUME = Engine.WIDTH * Point.WIDTH_FACTOR + Engine.HEIGHT;
    /**
     * The max rooms density.
     */
    private static final double MIN_ROOM_DENSITY = 0.005;
    /**
     * The max rooms density.
     */
    private static final double MAX_ROOM_DENSITY = 0.02;

    /**
     * Create an empty frame.
     */
    public Frame(Random rand, TileBrick[] tileBricks) {
        this.rooms = new ArrayList<>();
        this.hallways = new TreeMap<>();
        this.rand = rand;
        this.tileBricks = tileBricks;
    }

    /**
     * Create rooms and hallways.
     */
    public void create() {
        generateRooms();
        generateHallways();
    }

    /**
     * Generate new rooms.
     */
    public void generateRooms() {
        int nRooms = getRoomCounts(this.rand);
        generateRooms(nRooms);
    }

    /**
     * Generate new rooms with given room numbers.
     */
    public void generateRooms(int nRooms) {
        final int maxTry = 20;
        for (int i = 0; i < nRooms; i++) {
            int j = 0;
            while (j < maxTry) {
                Room newRoom = new Room(this.rand, this.tileBricks);
                if (newRoom.generateNewRoom()) {
                    this.rooms.add(newRoom);
                    break;
                }
                j++;
            }
        }
    }

    /**
     * Return how many rooms in this frame by a give random generator.
     */
    public static int getRoomCounts(Random rand) {
        int roomsLimit = (int) Math.round(Engine.HEIGHT * Engine.WIDTH * MAX_ROOM_DENSITY);
        int minRooms = (int) Math.round(Engine.HEIGHT * Engine.WIDTH * MIN_ROOM_DENSITY);
        return rand.nextInt(roomsLimit - minRooms) + minRooms;
    }

    /**
     * Generate new hallways by MST.
     */
    public void generateHallways() {
        KruskalForMst kfm = new KruskalForMst(this.rooms);
        List<Room[]> vertexes = kfm.generateVertexes();
        for (Room[] vertex : vertexes) {
            Hallway newHallway = new Hallway(vertex, this.tileBricks, this.hallways);
            this.hallways.put(newHallway.getKey(), newHallway);
        }
    }
}
