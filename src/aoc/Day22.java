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

    /*
                 ===== Option 2: trying to keep everything 2D

             1) do the movement until off the square
             2) apply the rotate to figure out where we end up
             3) rotate back to relative to initial
             4) go back to 1 until movement is done
     */
    private static long part2(String input, CubeDescription cubeDescription) {
        Puzzle puzzle = parseInput(input);

        // break the puzzle up into six squares
        SquareMapping[] squares = breakIntoSquares(puzzle.map, cubeDescription.n);
        if (DEBUG) {
            printSquares(cubeDescription.n, puzzle.map, squares);
        }

        // apply the movements


        return 0;
    }

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
                3 - 2 rotations
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
         */
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        System.out.println("================== sample ===============");
        part2(
            sampleInput, new CubeDescription(4,
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
                        new CubeConnection(1, 2), // above
                        new CubeConnection(5, 2), // right
                        new CubeConnection(3, 0), // down
                        new CubeConnection(2, 1)  // left
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
                        new CubeConnection(0, 2), // above
                        new CubeConnection(2, 0), // right
                        new CubeConnection(4, 2), // down
                        new CubeConnection(5, 3)  // left
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
                        new CubeConnection(0, 3), // above
                        new CubeConnection(3, 0), // right
                        new CubeConnection(4, 1), // down
                        new CubeConnection(1, 0)  // left
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
                        new CubeConnection(0, 0), // above
                        new CubeConnection(5, 3), // right
                        new CubeConnection(4, 0), // down
                        new CubeConnection(2, 0)  // left
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
                        new CubeConnection(3,0), // above
                        new CubeConnection(5,0), // right
                        new CubeConnection(1,2), // down
                        new CubeConnection(2,3)  // left
                    ),
            /*
            -----------------------------------------------
                3
             4  5  0
                1
                0 - 2 rotations
                1 - 1 rotation
                3 - 2 rotations
                4 - 0 rotations
                    */
                    List.of(
                        new CubeConnection(3,2), // above
                        new CubeConnection(0,2), // right
                        new CubeConnection(1,1), // down
                        new CubeConnection(4,0)  // left
                    )
                )
            )
        );

        System.out.println("================== real ===============");
        part2(realInput, new CubeDescription(50, null));

        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Solution: "
            + part1(realInput));
//
//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part2_expected.txt")));
//        System.out.println("Actual:   " +  part2(sampleInput));
//        System.out.println("Solution: " +  part2(realInput));

    }
}
