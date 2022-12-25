package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * --- Day 22: Monkey Map ---
 *
 * The monkeys take you on a surprisingly easy trail through the jungle. They're even going in
 * roughly the right direction according to your handheld device's Grove Positioning System.
 *
 * As you walk, the monkeys explain that the grove is protected by a force field. To pass through
 * the force field, you have to enter a password; doing so involves tracing a specific path on
 * a strangely-shaped board.
 *
 * At least, you're pretty sure that's what you have to do; the elephants aren't exactly fluent
 * in monkey.
 *
 * The monkeys give you notes that they took when they last saw the password entered
 * (your puzzle input).
 *
 * For example:
 *
 *         ...#
 *         .#..
 *         #...
 *         ....
 * ...#.......#
 * ........#...
 * ..#....#....
 * ..........#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * 10R5L5R10L4R5L5
 *
 * The first half of the monkeys' notes is a map of the board. It is comprised of a set of open
 * tiles (on which you can move, drawn .) and solid walls (tiles which you cannot enter, drawn #).
 *
 * The second half is a description of the path you must follow. It consists of alternating
 * numbers and letters:
 *
 *     A number indicates the number of tiles to move in the direction you are facing.
 *          If you run into a wall, you stop moving forward and continue with the next instruction.
 *     A letter indicates whether to turn 90 degrees clockwise (R) or counterclockwise (L).
 *          Turning happens in-place; it does not change your current tile.
 *
 * So, a path like 10R5 means "go forward 10 tiles, then turn clockwise 90 degrees,
 * then go forward 5 tiles".
 *
 * You begin the path in the leftmost open tile of the top row of tiles. Initially,
 * you are facing to the right (from the perspective of how the map is drawn).
 *
 * If a movement instruction would take you off of the map, you wrap around to the other
 * side of the board. In other words, if your next tile is off of the board,
 * you should instead look in the direction opposite of your current facing as
 * far as you can until you find the opposite edge of the board, then reappear there.
 *
 * For example, if you are at A and facing to the right, the tile in front of you is marked B;
 * if you are at C and facing down, the tile in front of you is marked D:
 *
 *         ...#
 *         .#..
 *         #...
 *         ....
 * ...#.D.....#
 * ........#...
 * B.#....#...A
 * .....C....#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * It is possible for the next tile (after wrapping around) to be a wall; this still
 * counts as there being a wall in front of you, and so movement stops before you actually
 * wrap to the other side of the board.
 *
 * By drawing the last facing you had with an arrow on each tile you visit,
 * the full path taken by the above example looks like this:
 *
 *         >>v#
 *         .#v.
 *         #.v.
 *         ..v.
 * ...#...v..v#
 * >>>v...>#.>>
 * ..#v...#....
 * ...>>>>v..#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * To finish providing the password to this strange input device, you need to
 * determine numbers for your final row, column, and facing as your final position
 * appears from the perspective of the original map. Rows start from 1 at the top and
 * count downward; columns start from 1 at the left and count rightward.
 * (In the above example, row 1, column 1 refers to the empty space with no tile on it in the
 * top-left corner.) Facing is 0 for right (>), 1 for down (v), 2 for left (<), and 3 for up (^).
 * The final password is the sum of 1000 times the row, 4 times the column, and the facing.
 *
 * In the above example, the final row is 6, the final column is 8, and the final facing is 0.
 * So, the final password is 1000 * 6 + 4 * 8 + 0: 6032.
 *
 * Follow the path given in the monkeys' notes. What is the final password?
 *
 * --- Part Two ---
 *
 * As you reach the force field, you think you hear some Elves in the distance.
 * Perhaps they've already arrived?
 *
 * You approach the strange input device, but it isn't quite what the monkeys drew in their notes.
 * Instead, you are met with a large cube; each of its six faces is a square of 50x50 tiles.
 *
 * To be fair, the monkeys' map does have six 50x50 regions on it. If you were to carefully
 * fold the map, you should be able to shape it into a cube!
 *
 * In the example above, the six (smaller, 4x4) faces of the cube are:
 *
 *         1111
 *         1111
 *         1111
 *         1111
 * 222233334444
 * 222233334444
 * 222233334444
 * 222233334444
 *         55556666
 *         55556666
 *         55556666
 *         55556666
 *
 * You still start in the same position and with the same facing as before, but the wrapping rules
 * are different. Now, if you would walk off the board, you instead proceed around the cube.
 * From the perspective of the map, this can look a little strange. In the above example,
 * if you are at A and move to the right,
 * you would arrive at B facing down; if you are at C and move down,
 * you would arrive at D facing up:
 *
 *         ...#
 *         .#..
 *         #...
 *         ....
 * ...#.......#
 * ........#..A
 * ..#....#....
 * .D........#.
 *         ...#..B.
 *         .....#..
 *         .#......
 *         ..C...#.
 *
 * Walls still block your path, even if they are on a different face of the cube.
 * If you are at E facing up, your movement is blocked by the wall marked by the arrow:
 *
 *         ...#
 *         .#..
 *      -->#...
 *         ....
 * ...#..E....#
 * ........#...
 * ..#....#....
 * ..........#.
 *         ...#....
 *         .....#..
 *         .#......
 *         ......#.
 *
 * Using the same method of drawing the last facing you had with an arrow on each tile you visit,
 * the full path taken by the above example now looks like this:
 *
 *         >>v#
 *         .#v.
 *         #.v.
 *         ..v.
 * ...#..^...v#
 * .>>>>>^.#.>>
 * .^#....#....
 * .^........#.
 *         ...#..v.
 *         .....#v.
 *         .#v<<<<.
 *         ..v...#.
 *
 * The final password is still calculated from your final position and facing from the
 * perspective of the map. In this example, the final row is 5, the final column is 7,
 * and the final facing is 3, so the final password is 1000 * 5 + 4 * 7 + 3 = 5031.
 *
 * Fold the map into a cube, then follow the path given in the monkeys' notes.
 * What is the final password?
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

    private static long part2(String input, Map<Integer, CubeMapping> cubeMapping) {
        Puzzle puzzle = parseInput(input);

        // break the puzzle up into six cubes

        //


        return 0;
    }

    public static void main(String[] args) throws IOException {
        int day = 22;

        DEBUG = true;
        /* TODO: map the sample and input by hand for the simulator to use
            X X 0
            1 2 3
            X X 4 5

            0 down -> <- up  3
            0 up
         */
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        part1(sampleInput);

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
