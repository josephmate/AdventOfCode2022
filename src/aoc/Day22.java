package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/22>Day 22: Monkey Map</a>
 */
public class Day22 {

    private static boolean DEBUG = false;

    interface Step {

    }

    record TurnClockwise() implements Step {

    }

    record TurnCounterClockwise() implements Step {

    }

    record Forward(int numSteps) implements Step {

    }

    record Puzzle(List<String> map, List<Step> steps ) {

    }

    record Coord(int r, int c) {

    }

    private static List<Step> parseSteps (String line) {
        line = line.trim();
        List<Step> result = new ArrayList<>();

        boolean hasVal = false;
        int current = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == 'L') {
                if (hasVal) {
                    result.add(new Forward(current));
                    hasVal = false;
                    current = 0;
                }
                result.add(new TurnCounterClockwise());
            } else if (c == 'R') {
                if (hasVal) {
                    result.add(new Forward(current));
                    hasVal = false;
                    current = 0;
                }
                result.add(new TurnClockwise());
            } else if (c >= '0' && c <='9') {
                hasVal = true;
                current = current * 10;
                current = current + (c-'0');
            } else {
                throw new IllegalStateException(line + " is not formatted correctly");
            }
        }

        if (hasVal) {
            result.add(new Forward(current));
        }

        return result;
    }

    private static Puzzle parseInput(String input) {
        String [] cols = input.split("\n\n");

        return new Puzzle(cols[0].lines().collect(Collectors.toList()),
            parseSteps(cols[1]));
    }

    private static Coord goForward(Coord currentCord, int direction, List<String> map) {
        // Facing is 0 for right (>), 1 for down (v), 2 for left (<), and 3 for up (^).
        return switch (direction) {
            case 0 -> {
                if (currentCord.c + 1 < map.get(currentCord.r).length()) {
                    yield new Coord(currentCord.r, currentCord.c + 1);
                } else {
                    yield new Coord(currentCord.r, 0);
                }
            }
            case 1 -> {
                int nextR = currentCord.r + 1;
                while (true) {
                    if (
                        nextR < map.size()
                        && currentCord.c < map.get(nextR).length()
                    ) {
                        yield new Coord(nextR, currentCord.c);
                    }
                    nextR++;
                    if (nextR >= map.size()) {
                        nextR = 0;
                    }
                }
            }
            case 2 -> {
                if (currentCord.c - 1 < 0) {
                    yield new Coord(currentCord.r, map.get(currentCord.r).length()-1);
                } else {
                    yield new Coord(currentCord.r, currentCord.c - 1);
                }
            }
            case 3 -> {
                int nextR = currentCord.r - 1;
                while (true) {
                    if (
                        nextR >= 0
                            && currentCord.c < map.get(nextR).length()
                    ) {
                        yield new Coord(nextR, currentCord.c);
                    }
                    nextR--;
                    if (nextR < 0) {
                        nextR = map.size()-1;
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected direction " + direction);
        };
    }

    private static void printMap(
        Step step,
        List<String> map,
        Map<Coord, Character> visited
    ) {
        if (!DEBUG) {
            return;
        }
        System.out.println("=============="+step+"================");
        for (int r = 0; r < map.size(); r++) {
            String row = map.get(r);
            for (int c = 0; c < row.length(); c++) {
                Coord coord = new Coord(r,c);
                Character visitedChar = visited.get(coord);
                if (visitedChar != null) {
                    System.out.print(visitedChar);
                } else {
                    System.out.print(row.charAt(c));
                }
            }
            System.out.println();
        }
    }

    private static long part1(String input) {
        Puzzle puzzle = parseInput(input);

        Map<Coord, Character> visited = new HashMap<>();

        // Facing is 0 for right (>), 1 for down (v), 2 for left (<), and 3 for up (^).
        int direction = 0;

        Coord currentCord = null;
        // find starting point
        for (int i = 0; i < puzzle.map.get(0).length(); i++) {
            char ch = puzzle.map.get(0).charAt(i);
            if (ch == '.') {
                currentCord = new Coord(0, i);
                break;
            }
        }
        if (DEBUG) {
            visited.put(currentCord, 'S');
        }

        printMap(null, puzzle.map, visited);

        for(Step step : puzzle.steps) {
            switch (step) {
                case Forward(int numSteps) -> {
                    int stepsConsumed = 0;
                    Coord nextCord = currentCord;
                    while(stepsConsumed < numSteps) {
                        nextCord = goForward(nextCord, direction, puzzle.map);

                        char ch = puzzle.map.get(nextCord.r).charAt(nextCord.c);
                        if (ch == '#') {
                            break; // going to hit a wall don't update anything
                        } else if (ch == ' ') {
                            continue; // dont update
                        } else if (ch == '.') {
                            currentCord = nextCord;
                            if (DEBUG) {
                                char directionChar = switch (direction) {
                                    case 0 -> '>';
                                    case 1 -> 'v';
                                    case 2 -> '<';
                                    case 3 -> '^';
                                    default -> throw new IllegalStateException();
                                };
                                visited.put(currentCord, directionChar);
                            }
                            stepsConsumed++;
                        } else {
                            throw new IllegalStateException("Unexpected character '" + ch + "' at " + nextCord);
                        }
                    }
                    printMap(step, puzzle.map, visited);
                }
                case TurnClockwise() -> {
                    direction = direction + 1;
                    if (direction > 3) {
                        direction = 0;
                    }
                }
                case TurnCounterClockwise() -> {
                    direction = direction - 1;
                    if (direction < 0) {
                        direction = 3;
                    }
                }
                default -> throw new IllegalStateException();
            }
        }

        // The final password is the sum of 1000 times the row, 4 times the column, and the facing.
        return 1000*(currentCord.r+1) + 4 * (currentCord.c+1) + (direction%4);
    }

    record CubeMapping(int up, int down, int left, int right) {

    }

    record SquareMapping(
        char[][] map,
        int r,
        int c
    ) {

    }

    private static char[][] grabSquare(List<String> lines, int size, int startR, int startC) {
        char[][] result = new char[size][size];
        for (int r = startR; r < startR + size; r++) {
            for (int c = startC; c < startC + size; c++) {
                result[r-startR][c-startC] = lines.get(r).charAt(c);
            }
        }

        return result;
    }

    private static SquareMapping[] breakIntoSquares(List<String> lines, int size) {
        SquareMapping[] result = new SquareMapping[6];

        // find the squares

        int squareId = 0;
        for (int r = 0; r < lines.size(); r+= size) {
            for(int c = 0; c < lines.get(r).length(); c+= size) {
                char ch = lines.get(r).charAt(c);
                if (ch == '#' || ch == '.') {
                    result[squareId] = new SquareMapping(grabSquare(lines, size, r, c), r, c);
                    squareId++;
                }
            }
        }


        return result;
    }

    private static int findSquareId(SquareMapping[] squareMappings, int size, int r, int c) {
        for (int i = 0; i < squareMappings.length; i++) {
            SquareMapping square = squareMappings[i];
            if (
                r >= square.r
                    && r < square.r + size
                    && c >= square.c
                    && c < square.c + size
            ) {
                return i;
            }
        }
        return -1;
    }

    private static SquareMapping findSquare(SquareMapping[] squareMappings, int size, int r, int c) {
        for (SquareMapping square : squareMappings) {
            if (
                r >= square.r
                && r < square.r + size
                && c >= square.c
                && c < square.c + size
            ) {
                return square;
            }
        }
        return null;
    }

    private static void printSquares(
        int size,
        List<String> lines,
        SquareMapping[] squareMappings
    ) {
        if (!DEBUG) {
           return;
        }
        for (int r = 0; r < lines.size(); r++) {
            for (int c = 0; c < lines.get(r).length(); c++) {
                SquareMapping square = findSquare(squareMappings, size, r, c);
                if (square == null) {
                    System.out.print(' ');
                } else {
                    System.out.print(square.map[r-square.r][c-square.c]);
                }
            }
            System.out.println();
        }
    }

    private static Coord rotateCounterclockwise(Coord originalCoord, int size, int numRotations) {
        Coord current = originalCoord;
        for (int i = 0; i < numRotations; i++) {
            current = rotateCounterclockwise(current, size);
        }
        return current;
    }

    private static Coord rotateCounterclockwise(Coord originalCoord, int size) {
        // 0,0  0,1  0,2  0,3
        // 1,0  1,1  1,2  1,3
        // 2,0  2,1  2,2  2,3
        // 3,0  3,1  3,2  3,3
        //
        // 0,3  1,3  2,3  3,3
        // 0,2  1,2  2,2, 3,2
        // 0,1  1,1  2,1  3,1
        // 0,0  1,0  2,0  3,0

        return new Coord( (size-1) - originalCoord.c, originalCoord.r);
    }

    private static final int DIRECTION_RIGHT = 0;
    private static final int DIRECTION_DOWN = 1;
    private static final int DIRECTION_LEFT = 2;
    private static final int DIRECTION_UP = 3;

    private static int figureOutDirectionBuggy(
        Coord rotated,
        CubeDescription cubeDescription
    ) {

        if (
            (rotated.r == 0 && rotated.c == 0)
            || (rotated.r == 0 && rotated.c == cubeDescription.n-1)
            || (rotated.r == cubeDescription.n-1 && rotated.c == 0)
            || (rotated.r == cubeDescription.n-1 && rotated.c == cubeDescription.n-1)
        ) {
            throw new IllegalStateException("rotate.r=" + rotated.r + " rotated.c=" + rotated.c + " is ambiguous");
        }

        final int nextDirection;
        if (rotated.r == 0) {
            nextDirection = DIRECTION_DOWN;
        } else if(rotated.r == cubeDescription.n-1) {
            nextDirection = 3;
        } else if (rotated.c == 0) {
            nextDirection = 0;
        } else if(rotated.c == cubeDescription.n-1) {
            nextDirection = 2;
        } else {
            throw new IllegalStateException();
        }

        return nextDirection;
    }

    record Movement(Coord nextCoord,
                    int nextDirection,
                    SquareMapping nextSquare,
                    int nextSquareId) {

    }

    private static int rotateDirectionCounterClockwise(int direction) {
        return switch (direction) {
            case 0 -> 3;
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            default -> throw new IllegalStateException("direction=" + direction);
        };
    }
    private static int rotateDirectionCounterClockwise(int direction,int times) {
        int result = direction;
        for (int i = 0; i < times; i++ ) {
            result = rotateDirectionCounterClockwise(result);
        }
        return result;
    }

    private static Movement transferSquares(
        CubeDescription cubeDescription,
        int squareId,
        int direction,
        Coord unRotated,
        SquareMapping[] squares
    ) {
        CubeConnection cubeConnection = cubeDescription.cubeConnections.get(squareId).get(direction);
        Coord rotated  = rotateCounterclockwise(
            unRotated, cubeDescription.n, cubeConnection.numRotations);
        int newDirection = rotateDirectionCounterClockwise(direction, cubeConnection.numRotations);
        SquareMapping nextSquare = squares[cubeConnection.cubeNum];
        return new Movement(
            new Coord(rotated.r + nextSquare.r, rotated.c + nextSquare.c),
            newDirection,
            nextSquare,
            cubeConnection.cubeNum
        );
    }

    /*
                 ===== Option 2: trying to keep everything 2D

             1) do the movement until off the square
             2) apply the rotate to figure out where we end up
             3) rotate back to relative to initial
             4) go back to 1 until movement is done
     */
    static long part2(String input, CubeDescription cubeDescription) {
        Puzzle puzzle = parseInput(input);

        // break the puzzle up into six squares
        SquareMapping[] squares = breakIntoSquares(puzzle.map, cubeDescription.n);
        printSquares(cubeDescription.n, puzzle.map, squares);

        // apply the movements


        Map<Coord, Character> visited = new HashMap<>();

        // Facing is 0 for right (>), 1 for down (v), 2 for left (<), and 3 for up (^).
        int direction = 0;

        int squareId = -1;
        SquareMapping currentSquare = null;
        Coord currentCord = null;
        // find starting point
        for (int i = 0; i < puzzle.map.get(0).length(); i++) {
            char ch = puzzle.map.get(0).charAt(i);
            if (ch == '.') {
                currentCord = new Coord(0, i);
                squareId = findSquareId(squares, cubeDescription.n, 0, i);
                currentSquare = squares[squareId];
                break;
            }
        }
        if (squareId == -1) {
            throw new IllegalStateException();
        }
        if (DEBUG) {
            visited.put(currentCord, 'S');
        }

        printMap(null, puzzle.map, visited);

        for(Step step : puzzle.steps) {
            switch (step) {
                case Forward(int numSteps) -> {
                    for (int i = 0; i < numSteps; i++) {
                        Movement nextMove = switch (direction) {
                            case 0 -> {  // '>'
                                if (currentCord.c - currentSquare.c + 1 < cubeDescription.n ) {
                                    yield new Movement(
                                        new Coord(currentCord.r, currentCord.c + 1),
                                        direction,
                                        currentSquare,
                                        squareId
                                    );
                                }
                                // we're outside the square. figure out which one we're going to
                                Coord unRotated = new Coord(currentCord.r - currentSquare.r, 0);
                                yield transferSquares(
                                    cubeDescription,
                                    squareId,
                                    direction,
                                    unRotated,
                                    squares);
                            }
                            case 1 -> { // 'v'
                                if (currentCord.r - currentSquare.r + 1 < cubeDescription.n ) {
                                    yield new Movement(
                                        new Coord(currentCord.r + 1, currentCord.c),
                                        direction,
                                        currentSquare,
                                        squareId
                                    );
                                }
                                // we're outside the square. figure out which one we're going to
                                Coord unRotated = new Coord(0, currentCord.c- currentSquare.c) ;
                                yield transferSquares(
                                    cubeDescription,
                                    squareId,
                                    direction,
                                    unRotated,
                                    squares);
                            }
                            case 2 -> { // '<'
                                if (currentCord.c - currentSquare.c - 1 >= 0 ) {
                                    yield new Movement(
                                        new Coord(currentCord.r, currentCord.c - 1),
                                        direction,
                                        currentSquare,
                                        squareId
                                    );
                                }
                                // we're outside the square. figure out which one we're going to
                                Coord unRotated = new Coord(
                                    currentCord.r - currentSquare.r,
                                    cubeDescription.n - 1) ;
                                yield transferSquares(
                                    cubeDescription,
                                    squareId,
                                    direction,
                                    unRotated,
                                    squares);
                            }
                            case 3 -> { // '^'
                                if (currentCord.r - 1 - currentSquare.r >= 0 ) {
                                    yield new Movement(
                                        new Coord(currentCord.r - 1, currentCord.c),
                                        direction,
                                        currentSquare,
                                        squareId
                                    );
                                }
                                // we're outside the square. figure out which one we're going to
                                Coord unRotated = new Coord(
                                    cubeDescription.n - 1,
                                    currentCord.c - currentSquare.c
                                    ) ;
                                yield transferSquares(
                                    cubeDescription,
                                    squareId,
                                    direction,
                                    unRotated,
                                    squares);

                            }
                            default -> throw new IllegalStateException();
                        };

                        char ch = puzzle.map.get(nextMove.nextCoord.r).charAt(nextMove.nextCoord.c);
                        if (ch == '#') {
                            break; // going to hit a wall don't update anything
                        } else if (ch == ' ') {
                            printMap(step, puzzle.map, visited);
                            throw new IllegalStateException(
                                "shouldn't hit an empty square"
                                + " step=" + step
                                + " nextMove=" + nextMove
                            );
                        } else if (ch == '.') {
                            currentCord = nextMove.nextCoord;
                            direction = nextMove.nextDirection;
                            currentSquare = nextMove.nextSquare;
                            squareId = nextMove.nextSquareId;
                            if (DEBUG) {
                                char directionChar = switch (direction) {
                                    case 0 -> '>';
                                    case 1 -> 'v';
                                    case 2 -> '<';
                                    case 3 -> '^';
                                    default -> throw new IllegalStateException();
                                };
                                visited.put(currentCord, directionChar);
                            }
                        } else {
                            throw new IllegalStateException("Unexpected character '" + ch + "' at " + nextMove.nextCoord);
                        }
                    }
                    printMap(step, puzzle.map, visited);
                }
                case TurnClockwise() -> {
                    direction = direction + 1;
                    if (direction > 3) {
                        direction = 0;
                    }
                }
                case TurnCounterClockwise() -> {
                    direction = direction - 1;
                    if (direction < 0) {
                        direction = 3;
                    }
                }
                default -> throw new IllegalStateException();
            }
        }

        int solution = 1000*(currentCord.r+1) + 4 * (currentCord.c+1) + (direction%4);
        if (DEBUG) {
            System.out.println(solution);
        }
        // The final password is the sum of 1000 times the row, 4 times the column, and the facing.
        return solution;
    }

    /**
     * <pre>
     * case 0 -> '>';
     * case 1 -> 'v';
     * case 2 -> '<';
     * case 3 -> '^';
     * </pre>
     * @param cubeNum
     * @param numRotations
     */
    record CubeConnection(
        int cubeNum,
        int numRotations
    ) {

    }

    record CubeDescription(
        int n,
        List<List<CubeConnection>> cubeConnections
    ) {

    }

    private static void incrementNestedMap(
        Map<Integer, Map<Integer, Integer>> edgeCounts,
        int a,
        int b
    ) {
        Map<Integer, Integer> subMap = edgeCounts.get(a);
        if (subMap == null) {
            subMap = new HashMap<>();
            edgeCounts.put(a, subMap);
        }
        subMap.put(b, subMap.getOrDefault(b,0) + 1);
    }

    private static void checkCubeDescription(CubeDescription cubeDescription) {
        Map<Integer, Integer> squareIdToCount = new HashMap<>();
        for(List<CubeConnection> connections : cubeDescription.cubeConnections) {
            if (connections.size() != 4) {
                throw new IllegalStateException("connections.size()=" + connections.size() + " but expected 4");
            }
            for (CubeConnection connection : connections) {
                squareIdToCount.put(connection.cubeNum, squareIdToCount.getOrDefault(connection.cubeNum, 0) + 1);
            }
        }

        if (squareIdToCount.size() != 6) {
            throw new IllegalStateException("squareIdToCount.size()=" + squareIdToCount.size() + " but expected 6");
        }

        squareIdToCount.forEach((squareId, count) -> {
            if (count != 4) {
                throw new IllegalStateException("expected square " + squareId + " to have 4 connections but had " + count);
            }
        });

        Map<Integer, Map<Integer, Integer>> edgeCounts = new HashMap<>();
        for(List<CubeConnection> connections : cubeDescription.cubeConnections) {
            // > right
            int i = 0;
            int direction = switch (connections.get(i).numRotations ) {
                // 0 > right
                // 1 v down
                // 2 < left
                // 3 ^ up
                case 0 -> 2;
                case 1 -> 1;
                case 2 -> 0;
                case 3 -> 3;
                default -> throw new IllegalStateException("unrecongnized number of rotations " + connections.get(0).numRotations);
            };
            incrementNestedMap(edgeCounts, connections.get(i).cubeNum, direction);
            i++;
            // v down
            direction = switch (connections.get(i).numRotations ) {
                // 0 > right
                // 1 v down
                // 2 < left
                // 3 ^ up
                case 0 -> 3;
                case 1 -> 2;
                case 2 -> 1;
                case 3 -> 0;
                default -> throw new IllegalStateException("unrecongnized number of rotations " + connections.get(0).numRotations);
            };
            incrementNestedMap(edgeCounts, connections.get(i).cubeNum, direction);
            i++;
            // < left
            direction = switch (connections.get(i).numRotations ) {
                // 0 > right
                // 1 v down
                // 2 < left
                // 3 ^ up
                case 0 -> 0;
                case 1 -> 3;
                case 2 -> 2;
                case 3 -> 1;
                default -> throw new IllegalStateException("unrecongnized number of rotations " + connections.get(0).numRotations);
            };
            incrementNestedMap(edgeCounts, connections.get(i).cubeNum, direction);
            i++;
            // ^ up
            direction = switch (connections.get(i).numRotations ) {
                // 0 > right
                // 1 v down
                // 2 < left
                // 3 ^ up
                case 0 -> 1;
                case 1 -> 0;
                case 2 -> 3;
                case 3 -> 2;
                default -> throw new IllegalStateException("unrecongnized number of rotations " + connections.get(0).numRotations);
            };
            incrementNestedMap(edgeCounts, connections.get(i).cubeNum, direction);
            i++;
        }

        if (edgeCounts.size() != 6) {
            throw new IllegalStateException("edgeCounts.size()=" + edgeCounts.size() + " but expected 6");
        }

        edgeCounts.forEach((squareId, subMap) -> {
            for(int i = 0; i <= 3; i++) {
                if (!subMap.containsKey(i)) {
                    System.err.println("squareId=" + squareId + " subMap missing key " + i + " subMap=" +subMap);
                } else if (subMap.get(i) != 1) {
                    System.err.println("squareId=" + squareId + " subMap has duplicated edge " + i + " subMap=" +subMap);
                }
            }

        });
    }

    static CubeDescription sampleCubeDescription = new CubeDescription(4,
        List.of(
                    /*
                1
             2  0  5
                3
               3 down to the top of 3, heading down
                    no rotation/ no flip
               2 left to the top of 2, heading down
                    rotate matrix clockwise
               1 up to the top of 1
                    rotate matrix clockwise twice
               5 right to the left of 5
                    rotate matrix clockwise twice
                    */
            List.of(
                new CubeConnection(5, 2), // > right
                new CubeConnection(3, 0), // v down
                new CubeConnection(2, 1), // < left
                new CubeConnection(1, 2)  // ^ above
            ),
            /*
            -----------------------------------------------
                0
             5  1  2
                4

                0 rotated twice
                2 no rotation
                4 rotated twice
                5 rotated three times
                    */
            List.of(
                new CubeConnection(2, 0), // right
                new CubeConnection(4, 2), // down
                new CubeConnection(5, 3),  // left
                new CubeConnection(0, 2) // above
            ),
            /*
            -----------------------------------------------
                0
             1  2  3
                4

                0 is rotated 3 times
                1 no rotation
                3 no rotation
                4 - 1 rotation
                    */
            List.of(
                new CubeConnection(3, 0), // right
                new CubeConnection(4, 1), // down
                new CubeConnection(1, 0),  // left
                new CubeConnection(0, 3) // above
            ),
            /*
            -----------------------------------------------
                0
             2  3  5
                4

                0 - 0 rotation
                2 - 0 rotation
                4 - 0 rotation
                5 - 3 rotations
                    */
            List.of(
                new CubeConnection(5, 3), // right
                new CubeConnection(4, 0), // down
                new CubeConnection(2, 0),  // left
                new CubeConnection(0, 0) // above
            ),
            /*
            -----------------------------------------------
                3
             2  4  5
                1

                1 - 2 rotations
                2 - 3 rotations
                3 - 0 rotations
                5 - 0 rotations
                    */
            List.of(
                new CubeConnection(5,0), // right
                new CubeConnection(1,2), // down
                new CubeConnection(2,3),  // left
                new CubeConnection(3,0) // above
            ),
            /*
            -----------------------------------------------
                3
             4  5  0
                1
                0 - 2 rotations
                1 - 1 rotation
                3 - 1 rotations
                4 - 0 rotations
                    */
            List.of(
                new CubeConnection(0,2), // right
                new CubeConnection(1,1), // down
                new CubeConnection(4,0), // left
                new CubeConnection(3,1)  // above
            )
        )
    );

    public static void main(String[] args) throws IOException {
        int day = 22;

        DEBUG = true;
        /* TODO: map the sample and input by hand for the simulator to use
            X X 0
            1 2 3
            X X 4 5

                                   back face is 1
                               +--------+                  +z
                              /        /|                  ^
                             /    4   / |                  |
                            +--------+  |    5             |
                            |        |  |                  |
                      2     |        |  +                   --------------> +x
                            |   3    | /                   /
                            |        |/                   /
                            +--------+                   /
                                 Floor is 0             v
                                                        +y

             ===== Option 1: Project everything into a 3d cube
                             then project back to 2d

             0 x->x, y->y, no z
             3 x->x, no y, y->z
             2



             ===== Option 2: trying to keep everything 2D

             1) do the movement until off the square
             2) apply the rotate to figure out where we end up
             3) rotate back to relative to initial
             4) go back to 1 until movement is done


            X X 0
            1 2 3
            X X 4 5
            -----------------------------------------------
                1
             2  0  5
                3
               3 down to the top of 3, heading down
                    no rotation/ no flip
               2 left to the top of 2, heading down
                    rotate matrix clockwise
               1 up to the top of 1
                    rotate matrix clockwise twice
               5 right to the left of 5
                    rotate matrix clockwise twice
            -----------------------------------------------
                0
             5  1  2
                4

                0 rotated twice
                2 no rotation
                4 rotated twice
                5 rotated three times
            -----------------------------------------------
                0
             1  2  3
                4

                0 is rotated 3 times
                1 no rotation
                3 no rotation
                4 - 1 rotation
            -----------------------------------------------
                0
             2  3  5
                4

                0 - 0 rotation
                2 - 0 rotation
                4 - 0 rotation
                5 - 3 rotations
            -----------------------------------------------
                3
             2  4  5
                1

                1 - 2 rotations
                2 - 3 rotations
                3 - 0 rotations
                5 - 0 rotations
            -----------------------------------------------
                3
             4  5  0
                1
                0 - 2 rotations
                1 - 1 rotation
                3 - 1 rotations
                4 - 0 rotations

             ===== Option 3: tweak existing part1 to work

            X X 0
            1 2 3
            X X 4 5

            0 -> 2 
            1 - 0
         */
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        /*
         * X 0 1
         * X 2 X
         * 3 4
         * 5
         * ----------------------
         *   5     X 0 1
         * 3 0 1   X 2 X
         *   2     3 4
         *         5
         *   > : 1 - 0 rotations
         *   v : 2 - 0 rotations
         *   < : 3 - 2 rotations
         *   ^ : 5 - 3 rotations
         * ----------------------
         *   5     X 0 1
         * 0 1 4   X 2 X
         *   2     3 4
         *         5
         *   > : 4 - 2 rotations
         *   v : 2 - 3 rotations
         *   < : 0 - 0 rotations
         *   ^ : 5 - 0 rations
         * ----------------------
         *   0     X 0 1
         * 3 2 1   X 2 X
         *   4     3 4
         *         5
         *   > : 1 - 1 rotation
         *   v : 4 - 0 rotations
         *   < : 3 - 1 rotation
         *   ^ : 0 - 0 rotations
         * ----------------------
         *   2     X 0 1
         * 0 3 4   X 2 X
         *   5     3 4
         *         5
         *   > : 4 - 0 rotations
         *   v : 5 - 0 rotations
         *   < : 0 - 2 rotations
         *   ^ : 2 - 3 rotations
         * ----------------------
         *   2     X 0 1
         * 3 4 1   X 2 X
         *   5     3 4
         *         5
         *   > : 1 - 2 rotations
         *   v : 5 - 3 rotations
         *   < : 3 - 0 rotations
         *   ^ : 2 - 0 rotations
         * ----------------------
         *   3     X 0 1
         * 0 5 4   X 2 X
         *   1     3 4
         *         5
         *   > : 4 - 1 rotations
         *   v : 1 - 0 rotations
         *   < : 0 - 1 rotations
         *   ^ : 3 - 0 rotations
         */
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        System.out.println("================== sample ===============");

        part2(
            sampleInput, sampleCubeDescription
        );
        CubeDescription realCubeDescription = new CubeDescription(50,
            List.of(
                /* ----------------------
                 *   5     X 0 1
                 * 3 0 1   X 2 X
                 *   2     3 4
                 *         5
                 *   > : 1 - 0 rotations
                 *   v : 2 - 0 rotations
                 *   < : 3 - 2 rotations
                 *   ^ : 5 - 3 rotations
                 */
                List.of(
                    new CubeConnection(1,0), // right
                    new CubeConnection(2,0), // down
                    new CubeConnection(3,2), // left
                    new CubeConnection(5,3)  // above
                ),
                /* ----------------------
                 *   5     X 0 1
                 * 0 1 4   X 2 X
                 *   2     3 4
                 *         5
                 *   > : 4 - 2 rotations
                 *   v : 2 - 3 rotations
                 *   < : 0 - 0 rotations
                 *   ^ : 5 - 0 rations
                 */
                List.of(
                    new CubeConnection(4,2), // right
                    new CubeConnection(2,3), // down
                    new CubeConnection(0,0), // left
                    new CubeConnection(5,0)  // above
                ),
                /* ----------------------
                 *   0     X 0 1
                 * 3 2 1   X 2 X
                 *   4     3 4
                 *         5
                 *   > : 1 - 1 rotation
                 *   v : 4 - 0 rotations
                 *   < : 3 - 1 rotation
                 *   ^ : 0 - 0 rotations
                 */
                List.of(
                    new CubeConnection(1,1), // right
                    new CubeConnection(4,0), // down
                    new CubeConnection(3,1), // left
                    new CubeConnection(0,0)  // above
                ),
                /* ----------------------
                 *   2     X 0 1
                 * 0 3 4   X 2 X
                 *   5     3 4
                 *         5
                 *   > : 4 - 0 rotations
                 *   v : 5 - 0 rotations
                 *   < : 0 - 2 rotations
                 *   ^ : 2 - 3 rotations
                 */
                List.of(
                    new CubeConnection(4,0), // right
                    new CubeConnection(5,0), // down
                    new CubeConnection(0,2), // left
                    new CubeConnection(2,3)  // above
                ),
                /* ----------------------
                 *   2     X 0 1
                 * 3 4 1   X 2 X
                 *   5     3 4
                 *         5
                 *   > : 1 - 2 rotations
                 *   v : 5 - 3 rotations
                 *   < : 3 - 0 rotations
                 *   ^ : 2 - 0 rotations
                 */
                List.of(
                    new CubeConnection(1,2), // right
                    new CubeConnection(5,3), // down
                    new CubeConnection(3,0), // left
                    new CubeConnection(2,0)  // above
                ),
                /* ----------------------
                 *   3     X 0 1
                 * 0 5 4   X 2 X
                 *   1     3 4
                 *         5
                 *   > : 4 - 1 rotations
                 *   v : 1 - 0 rotations
                 *   < : 0 - 1 rotations
                 *   ^ : 3 - 0 rotations
                 */
                List.of(
                    new CubeConnection(4,1), // right
                    new CubeConnection(1,0), // down
                    new CubeConnection(0,1), // left
                    new CubeConnection(3,0)  // above
                )
            ));

        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Solution: "
            + part1(realInput));

        System.out.println("Checking sample");
        checkCubeDescription(sampleCubeDescription);
        System.out.println("Checking real");
        checkCubeDescription(realCubeDescription);
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part2_expected.txt")));
        System.out.println("Actual:   " +  part2(sampleInput, sampleCubeDescription));
        System.out.println("Solution: " +  part2(realInput, realCubeDescription));

    }
}
