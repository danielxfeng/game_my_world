# My World Design Document

Author: Daniel Feng

---

# **1. Classes and Data Structures**

---

## Main

This is the entry point to our program. 

It takes in arguments from the command line and based on the command calls the corresponding codes in `Engine` which will either accept the command by keyboard or `String`.

It also validates the arguments based on the command to ensure that enough arguments were passed in.

---

## Engine

This class is used to response the user command.

The method `interactWithKeyboard` responds the commands from keyboard.

The method `interactWithInputString` responds the commands from the given `String`.

The method `parseInput` defer the commands to class `Game`.

The methods `showMainMenu`, `showPrompt`,`showSeed` are used for showing the main menu.

### Fields

- `WIDTH` : the current working folder.
- `HEIGHT` : the persistence folder: .git
- `INFO-HEIGHT` : the folder for serialised objects.
- `PAUSE_TIME`: the pause time after a frame showed.
- *`VISION_SCOPE`: the vision scope of the player of game*
- `game` : a  instance of the `Game` class.

---

## Game

This class is the main logical code of the game. This class will keep the game status, render the frame, play the game, and defer the word initialising tasks to class `Frame`.

### Fields

- `iPointOfPlayer` : saves the position of a player. Will be serialised.
- `visionScope` : saves the current player vision scope. Will be serialised.
- `tileBricks`   : saves the status of the game. Will be serialised.
- `tiles`: the frame of the game.
- `ter`: the render of the game.
- `rand`: a random number generator.

---

## TileBrick

This class manages the properties of every tile. 

### Fields

- `type` : a tile is either a `wall` or `brick` or `gate` or `player`.
- `constructionType` : the construction of the brick is either a *`ROOM`, a* *`HALLWAY` or just* *`NOTHING`.*
- `constructionKey`: the *`UUID` of the construction.*
- `hideOne`: a linked list like data structure to save the original tile properties when is occupied by a `player`.

---

## Point

This class manages the coordinate system of the game.

### Fields

- `x` : the x coordinate of a point.
- `y` : the x coordinate of a point.
- `DEFAULT_BRANCH` : set to `master` as default value.
- `branches` : the `TreeMap` keeps the branches’ name and the hash codes of their head point.
- `currBranch` : tracks the current active branch.

---

## Utils, RandomUtils

The two supportive classes are provided by UC Berkeley. 

---

# Package World

Classes for the creating a world.

---

## Frame

This class is the main class to create a world. 

The creating process are:

1. Defer to class `Room` first to build some rooms randomly. All rooms should NOT share any point.
2. For insuring every room should be connected with other rooms, use the `Kruskal Algorithm`  that was implemented in class `KruskalForMST` to find out the hallways requirements.
3. Defer to class `Hallway` to build hallways one by one.

### Fields

- `tileBricks`: the field is from the class `Game`.
- `rooms`: saves the list of `rooms`.
- `hallways`: saves the list of `hallways`.

---

## Construction

This class defines a construction including `Room` and `Hallway`. 

### Fields

- `key`: the `UUID`of a construction.
- `walls`: saves the point list of walls.
- `bricks`: saves the point list of bricks.
- `gates`: saves the point list of gates.
- `tileBricks`: the field is from the class `Game`.

---

## Room

This class defines a room, that is also a sub-class of `Construction`.

### Fields

- `sw`: the southwest corner of a room
- `width`: the width of a room.
- `height`: the height of a room.

---

## Hallway

This class defines a hallway, that is also a sub-class of `Construction`. The process of building uses the `A* Algorithm` that is implemented in class `AStar` to find out the route between 2 rooms.

### Fields

- `startRoom`: the start room of a hallway.
- `targetRoom`: the target room of a hallway.
- `connectedRooms`: the list that saves the rooms that the hallway connects.

---

## KruskalForMST

This class is a Kruskal Algorithm implementation to solve the MST problem:

This class is to support the class `Frame` to find out the requirements of hallways between rooms after all rooms were randomly built.

### Fields

- `queue`: a priority queue of the Kruskal Algorithm.

### Nested Class: DisjointSet

This class is an implementation of DJS data structure that used for `Kruskal` Algorithm:

### Fields

- `points`: the array to save the data structure.

### Nested Class: VertexOfPoints

This class is a data structure to save 2 rooms and their distance for the priority queue of Kruskal Algorithm.

### Fields

- `dist`: the Manhattan distance of 2 rooms.
- `room1`: the 1st room of a vertex.
- `room2`: the 2nd room of a vertex.

---

## AStar

This class is an implementation of A* Algorithm that used for class `Hallway` to find out the ways between 2 rooms:

### Fields

- `startRoom`: the start room of the hallway.
- `targetRoom`: the target room of the hallway.
- `deque`: the priority queue of the algorithm.
- `edges`: an array of edges.

### Nested Class: Edge

This class is a data structure to save the properties of edge of an A* algorithm.

### Fields

- `index`: the index of the edge.
- `distTo`: the distTo of the edge, means the passed steps.
- `h`: the h of the edge, means the Manhattan distance to the target room.
- `priority`: is distTo + h.
- `prev`: the previous Edge.

---

# Package InputPackage

Classes for dealing with the user input.

---

## InputMethods

The interface of input.

---

## KeyInput

This class is an implementation of  `InputMethods` to deal with the keyboard input.

### Fields

- `startRoom`: the start room of the hallway.
- `targetRoom`: the target room of the hallway.
- `deque`: the priority queue of the algorithm.
- `edges`: an array of edges.

---

## StringInput

This class is an implementation of  `InputMethods` to deal with the input by given a `String`.

### Fields

- `str`: the input string.
- `index`: the current index of the string.

---

# Package TileEngine

There are 3 classes `TERenderer`, `TETile`,`Tileset` that were provided by UC Berkeley to deal with the frame render and tile style etc.

---

# **2. Algorithms**

---

## Kruskal

Use the Kruskal Algorithm for MST(minimal spinning tree) to calculate the requirements of the hallways.

1. Calculate the distance between every rooms.
2. Add the paired room and the distance to the priority queue.
3. Poll the queue and add the not visited and not connected pair to the result list.

---

## DisjointSet

Use the Disjoint Set data structure to implement the quick find, quick connect Algorithm.

1. Build an array to save the disjoint set data.
2. Use recursive call to implement the quick find, connect, isConnect methods.

---

## A*

Use the A* algorithm to find the path of the hallway.

---

# **3. Persistence**

---

Only one file called “`my_world.obj`” will be saved in the program dictionary when user save the status.