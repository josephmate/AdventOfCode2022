package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/17>Day 17: Pyroclastic Flow </a>
 */
public class Day17 {

    private static boolean DEBUG = false;

    private interface Collidable {

        List<Coord> points();

        default boolean collides(Set<Coord> rocks, int leftWall, int rightWall, int floor) {
            List<Coord> coords = points();

            for (Coord coord : coords) {
                if (rocks.contains(coord)) {
                    return true;
                }
                if (coord.x <= leftWall) {
                    return true;
                }
                if (coord.x >= rightWall) {
                    return true;
                }
                if (coord.y <= floor) {
                    return true;
                }
            }

            return false;
        }

        Collidable start(int highestRock, int leftWall, int rightWall) ;

        Collidable moveLeft();
        Collidable moveRight();
        Collidable down();
    }

    /**
     * C###
     */
    record Minus(Coord coord) implements Collidable {
        @Override
        public List<Coord> points() {
            return List.of(
                coord,
                new Coord(coord.x + 1, coord.y),
                new Coord(coord.x + 2, coord.y),
                new Coord(coord.x + 3, coord.y)
            );
        }

        /**
         *   The first rock begins falling:
         *   |..C@@@.|
         *   |.......|
         *   |.......|
         *   |.......|
         *   +-------+
         *
         *  -101234567
         *   |..C###.| +4
         *   |  .    | +3
         *   |  .    | +2
         *   |  .    | +1
         *    #######  0
         *
         * Each rock appears so that its left
         * edge is two units away from the left wall and its bottom edge is three units above the highest
         * rock in the room (or the floor, if there isn't one).
         */
        public Collidable start(int highestRock, int leftWall, int rightWall) {
            return new Minus(new Coord(2, highestRock+4));
        }

        @Override
        public Collidable moveLeft() {
            return new Minus(new Coord(coord.x-1, coord.y));
        }

        @Override
        public Collidable moveRight() {
            return new Minus(new Coord(coord.x+1, coord.y));
        }

        @Override
        public Collidable down() {
            return new Minus(new Coord(coord.x, coord.y-1));
        }
    }

    /**
     * .#.
     * #C#
     * .#.
     *
     */
    record Plus(Coord coord) implements Collidable {
        @Override
        public List<Coord> points() {
            return List.of(
                coord,
                new Coord(coord.x + 1, coord.y),
                new Coord(coord.x - 1, coord.y),
                new Coord(coord.x + 0, coord.y + 1),
                new Coord(coord.x + 0, coord.y - 1)
            );
        }

        /**
         *  0123
         * |...@...|
         * |..@C@..| 5
         * |...@...| 4
         * |.......| 3
         * |.......| 2
         * |.......| 1
         * |..####.|
         * +-------+
         *
         *
         * Each rock appears so that its left
         * edge is two units away from the left wall and its bottom edge is three units above the highest
         * rock in the room (or the floor, if there isn't one).
         */
        public Collidable start(int highestRock, int leftWall, int rightWall) {
            return new Plus(new Coord(3, highestRock+5));
        }
        @Override
        public Collidable moveLeft() {
            return new Plus(new Coord(coord.x-1, coord.y));
        }

        @Override
        public Collidable moveRight() {
            return new Plus(new Coord(coord.x+1, coord.y));
        }

        @Override
        public Collidable down() {
            return new Plus(new Coord(coord.x, coord.y-1));
        }
    }

    /**
     * ..#
     * ..#
     * C##
     */
    record L(Coord coord) implements Collidable {
        @Override
        public List<Coord> points() {
            return List.of(
                coord,
                new Coord(coord.x + 1, coord.y),
                new Coord(coord.x + 2, coord.y),
                new Coord(coord.x + 2, coord.y + 1),
                new Coord(coord.x + 2, coord.y + 2)
            );
        }

        /**
         * ..#
         * ..#
         * C##
         *  -101234567
         *   |....#..| +6
         *   |....#..| +5
         *   |..C##..| +4
         *   |  .    | +3
         *   |  .    | +2
         *   |  .    | +1
         *    #######  0
         * The tall, vertical chamber is exactly seven units wide. Each rock appears so that its left
         * edge is two units away from the left wall and its bottom edge is three units above the highest
         * rock in the room (or the floor, if there isn't one).
         */
        public Collidable start(int highestRock, int leftWall, int rightWall) {
            return new L(new Coord(2, 4+highestRock));
        }

        @Override
        public Collidable moveLeft() {
            return new L(new Coord(coord.x-1, coord.y));
        }

        @Override
        public Collidable moveRight() {
            return new L(new Coord(coord.x+1, coord.y));
        }

        @Override
        public Collidable down() {
            return new L(new Coord(coord.x, coord.y-1));
        }
    }

    /**
     * #
     * #
     * #
     * O
     */
    record I(Coord coord) implements Collidable {
        @Override
        public List<Coord> points() {
            return List.of(
                coord,
                new Coord(coord.x , coord.y + 1),
                new Coord(coord.x , coord.y + 2),
                new Coord(coord.x , coord.y + 3)
            );
        }

        /**
         *  -101234567
         *   |..#....| +7
         *   |..#....| +6
         *   |..#....| +5
         *   |..C....| +4
         *   |  .    | +3
         *   |  .    | +2
         *   |  .    | +1
         *    #######  0
         * The tall, vertical chamber is exactly seven units wide. Each rock appears so that its left
         * edge is two units away from the left wall and its bottom edge is three units above the highest
         * rock in the room (or the floor, if there isn't one).
         */
        public Collidable start(int highestRock, int leftWall, int rightWall) {
            return new I(new Coord(2, 4+highestRock));
        }
        @Override
        public Collidable moveLeft() {
            return new I(new Coord(coord.x-1, coord.y));
        }

        @Override
        public Collidable moveRight() {
            return new I(new Coord(coord.x+1, coord.y));
        }

        @Override
        public Collidable down() {
            return new I(new Coord(coord.x, coord.y-1));
        }
    }

    /**
     * ##
     * O#
     */
    record Square(Coord coord) implements Collidable {
        @Override
        public List<Coord> points() {
            return List.of(
                coord,
                new Coord(coord.x , coord.y + 1),
                new Coord(coord.x + 1, coord.y),
                new Coord(coord.x + 1, coord.y + 1)
            );
        }

        /**
         *  -101234567
         *   |..##...| +5
         *   |..C#...| +4
         *   |  .    | +3
         *   |  .    | +2
         *   |  .    | +1
         *    #######  0
         * The tall, vertical chamber is exactly seven units wide. Each rock appears so that its left
         * edge is two units away from the left wall and its bottom edge is three units above the highest
         * rock in the room (or the floor, if there isn't one).
         */
        public Collidable start(int highestRock, int leftWall, int rightWall) {
            return new Square(new Coord(2, 4+highestRock));
        }

        @Override
        public Collidable moveLeft() {
            return new Square(new Coord(coord.x-1, coord.y));
        }

        @Override
        public Collidable moveRight() {
            return new Square(new Coord(coord.x+1, coord.y));
        }

        @Override
        public Collidable down() {
            return new Square(new Coord(coord.x, coord.y-1));
        }
    }

    record Coord (int x, int y) {

    }

    private static void printState(Set<Coord> rocks) {
        int minX = Math.min(-1, rocks.stream().mapToInt(Coord::x).min().orElseThrow());
        int maxX = Math.max(7, rocks.stream().mapToInt(Coord::x).max().orElseThrow());
        int minY = Math.min(-1, rocks.stream().mapToInt(Coord::y).min().orElseThrow());
        int maxY = rocks.stream().mapToInt(Coord::y).max().orElseThrow();

        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                if (rocks.contains(new Coord(x,y))) {
                    System.out.print('#'); // ROCK
                } else if (y == -1) {
                    System.out.print('-'); // FLOOR
                } else if (x == -1) {
                    System.out.print('|'); // WALL
                } else if (x == 7) {
                    System.out.print('|'); // WALL
                } else {
                    System.out.print('.'); // NOTHING
                }
            }
            System.out.println();
        }
    }

    private static void printState(
        Map<Coord, Integer> rocks,
        Map<Integer, SortedSet<Long>> yToRockNum
    ) {
        int minX = Math.min(-1, rocks.keySet().stream().mapToInt(Coord::x).min().orElseThrow());
        int maxX = Math.max(7, rocks.keySet().stream().mapToInt(Coord::x).max().orElseThrow());
        int minY = Math.min(-1, rocks.keySet().stream().mapToInt(Coord::y).min().orElseThrow());
        int maxY = rocks.keySet().stream().mapToInt(Coord::y).max().orElseThrow();

        for (int y = maxY; y >= minY; y--) {
            System.out.print((y+1) + " ");
            for (int x = minX; x <= maxX; x++) {
                Coord coord = new Coord(x,y);
                if (rocks.containsKey(coord)) {
                    System.out.print(rocks.get(coord)); // ROCK
                } else if (y == -1) {
                    System.out.print('-'); // FLOOR
                } else if (x == -1) {
                    System.out.print('|'); // WALL
                } else if (x == 7) {
                    System.out.print('|'); // WALL
                } else {
                    System.out.print('.'); // NOTHING
                }
            }
            SortedSet<Long> rockNums = yToRockNum.get(y);
            if (rockNums != null) {
                System.out.print(" ");
                System.out.print(String.join(", ", rockNums.stream().map(String::valueOf).collect(Collectors.toList())));
            }
            System.out.println();
        }
    }

    private static long part1(String input) {
        final String jets = input.trim();

        Set<Coord> rocks = new HashSet<>();

        // The tall, vertical chamber is exactly seven units wide.
        // left wall is -1
        // chamber is 0 to 6
        // right wall is 7
        final int LEFT_WALL = -1;
        final int RIGHT_WALL = 7;
        final int FLOOR = -1;
        int highestRock = FLOOR;

        List<Collidable> rockMakers = List.of(
            new Minus(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new Plus(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new L(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new I(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new Square(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE))
        );

        int jet = 0;
        for (int rock = 0; rock < 2022; rock++) {
            // and a new rock immediately begins falling.
            Collidable currentRock = rockMakers.get(rock% rockMakers.size()).start(highestRock, LEFT_WALL, RIGHT_WALL);

            if (DEBUG) {
                if (rock >= 1 && rock <= 11) {
                    System.out.println("========= "+rock+" =========");
                    printState(rocks);
                }
            }

            while (true) {
                /* After a rock appears, it alternates between being pushed by a jet of hot gas one unit
                 * (in the direction indicated by the next symbol in the jet pattern)
                 */
                /*
                 * In jet patterns, < means a push to the left, while > means a push to the right. The pattern
                 * above means that the jets will push a falling rock right, then right, then right, then left,
                 * then left, then right, and so on. If the end of the list is reached, it repeats.
                 */
                char jetDirection = jets.charAt(jet % jets.length());
                final Collidable pushedRock;
                switch (jetDirection) {
                    case '<' -> {
                        pushedRock = currentRock.moveLeft();
                    }
                    case '>' -> {
                        pushedRock = currentRock.moveRight();
                    }
                    default -> throw new IllegalStateException("jet at " + jet % jets.length() + " not valid: " + jetDirection);
                }
                jet++;

                //  If any movement would cause any part of the rock to move into the walls, floor, or a
                //                 * stopped rock, the movement instead does not occur.
                if (!pushedRock.collides(rocks, LEFT_WALL, RIGHT_WALL, FLOOR)) {
                    currentRock = pushedRock;
                }

                //
                final Collidable fallenRock = currentRock.down();
                /* and then falling one unit
                 * down. If a downward movement would have caused a
                 * falling rock to move into the floor or an already-fallen rock, the falling rock stops where
                 * it is (having landed on something) and a new rock immediately begins falling.
                 */
                if (fallenRock.collides(rocks, LEFT_WALL, RIGHT_WALL, FLOOR)) {
                    highestRock = Math.max(highestRock,
                        currentRock.points().stream().mapToInt(Coord::y).max().orElseThrow());
                    rocks.addAll(currentRock.points());
                    break;
                }
                currentRock = fallenRock;
            }
        }

        if (DEBUG) {
            System.out.println("========= 2022 =========");
            printState(rocks);
        }

        return highestRock + 1;
    }

    private static void countRockLines(
        Map<Coord, Integer> rocks
    ) {
        int minX = Math.min(-1, rocks.keySet().stream().mapToInt(Coord::x).min().orElseThrow());
        int maxX = Math.max(7, rocks.keySet().stream().mapToInt(Coord::x).max().orElseThrow());
        int minY = Math.min(-1, rocks.keySet().stream().mapToInt(Coord::y).min().orElseThrow());
        int maxY = rocks.keySet().stream().mapToInt(Coord::y).max().orElseThrow();

        Map<String, Long> counts = new HashMap<>();

        for (int y = maxY; y >= minY; y--) {
            StringBuilder sb = new StringBuilder();
            for (int x = minX; x <= maxX; x++) {
                Coord coord = new Coord(x,y);
                if (rocks.containsKey(coord)) {
                    sb.append(rocks.get(coord)); // ROCK
                } else if (y == -1) {
                    sb.append('-'); // FLOOR
                } else if (x == -1) {
                    sb.append('|'); // WALL
                } else if (x == 7) {
                    sb.append('|'); // WALL
                } else {
                    sb.append('.'); // NOTHING
                }
            }
            String line = sb.toString();
            Long count = counts.get(line);
            if (count == null) {
                count = 0L;
            }
            counts.put(line, count+1);
        }

        List<Map.Entry<String, Long>> toSort = new ArrayList<>(counts.entrySet());
        Collections.sort(toSort,
            Comparator.comparingLong((Map.Entry<String, Long> a) -> a.getValue())
                       .thenComparing(Map.Entry::getKey));
        for (var entry : toSort) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
    }

    private static long part2(
        String input,
        long limit

    ) {
        final String jets = input.trim();

        Set<Coord> rocks = new HashSet<>();
        Map<Coord, Integer> rockMap = new HashMap<>();
        Map<Integer, SortedSet<Long>> yToRockNum = new HashMap<>();

        // option 2 seems easier...
        // option 1 still doesn't seem fast enough since 1000000000000L is like solving O(N) 2^39

        // The tall, vertical chamber is exactly seven units wide.
        // left wall is -1
        // chamber is 0 to 6
        // right wall is 7
        final int LEFT_WALL = -1;
        final int RIGHT_WALL = 7;
        final int FLOOR = -1;
        int highestRock = FLOOR;

        List<Collidable> rockMakers = List.of(
            new Minus(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new Plus(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new L(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new I(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE)),
            new Square(new Coord(Integer.MIN_VALUE, Integer.MIN_VALUE))
        );

        long jet = 0;
        for (long rock = 0; rock < limit; rock++) {
            // and a new rock immediately begins falling.
            Collidable currentRock = rockMakers.get((int) (rock % rockMakers.size())).start(highestRock, LEFT_WALL, RIGHT_WALL);

            while (true) {
                /* After a rock appears, it alternates between being pushed by a jet of hot gas one unit
                 * (in the direction indicated by the next symbol in the jet pattern)
                 */
                /*
                 * In jet patterns, < means a push to the left, while > means a push to the right. The pattern
                 * above means that the jets will push a falling rock right, then right, then right, then left,
                 * then left, then right, and so on. If the end of the list is reached, it repeats.
                 */
                char jetDirection = jets.charAt((int) (jet % jets.length()));
                final Collidable pushedRock;
                switch (jetDirection) {
                    case '<' -> {
                        pushedRock = currentRock.moveLeft();
                    }
                    case '>' -> {
                        pushedRock = currentRock.moveRight();
                    }
                    default -> throw new IllegalStateException("jet at " + jet % jets.length() + " not valid: " + jetDirection);
                }
                jet++;

                //  If any movement would cause any part of the rock to move into the walls, floor, or a
                //                 * stopped rock, the movement instead does not occur.
                if (!pushedRock.collides(rocks, LEFT_WALL, RIGHT_WALL, FLOOR)) {
                    currentRock = pushedRock;
                }

                //
                final Collidable fallenRock = currentRock.down();
                /* and then falling one unit
                 * down. If a downward movement would have caused a
                 * falling rock to move into the floor or an already-fallen rock, the falling rock stops where
                 * it is (having landed on something) and a new rock immediately begins falling.
                 */
                if (fallenRock.collides(rocks, LEFT_WALL, RIGHT_WALL, FLOOR)) {
                    highestRock = Math.max(highestRock,
                        currentRock.points().stream().mapToInt(Coord::y).max().orElseThrow());
                    rocks.addAll(currentRock.points());
                    for (Coord rockPoint : currentRock.points()) {
                        rockMap.put(rockPoint, (int) (rock % rockMakers.size()) );
                        SortedSet<Long> rockNumsOnRow = yToRockNum.computeIfAbsent(rockPoint.y, (y) -> new TreeSet<>());
                        rockNumsOnRow.add(rock+1);
                    }
                    break;
                }
                currentRock = fallenRock;
            }
        }

        if (DEBUG) {

            System.out.println("==========================");
            printState(rockMap, yToRockNum);
            System.out.println("==========================");
            countRockLines(rockMap);
        }

        return highestRock + 1;
    }

    private static void manuallySolvePart2(
        long firstLoopY,
        long firstLoopRock,
        long secondLoopY,
        long secondLoopRock,
        long leftOverY
    ) {

        // calculation
        long heightSoFar = 0;
        long remaining = 1000000000000L;
        System.out.println("Starting with " + remaining);
        remaining -= firstLoopRock;
        heightSoFar += firstLoopY;
        System.out.println("After the first loop we still have " + remaining + " with height " + heightSoFar);
        long yDelta = secondLoopY-firstLoopY;
        long rockDelta = secondLoopRock-firstLoopRock;
        long fullLoops = remaining / rockDelta;
        heightSoFar += fullLoops * yDelta;
        remaining = remaining % rockDelta;
        System.out.println("After consuming the loops we have " + remaining + " with height " + heightSoFar);
        System.out.println("We should look at rock " + (firstLoopRock + remaining) + " to see how much we have");
        long rockLeftOverHeight = leftOverY - firstLoopY;
        heightSoFar+= rockLeftOverHeight;
        System.out.println("Actual:   " + heightSoFar);
    }

    public static void main(String[] args) throws IOException {

        DEBUG = true;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_17_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_17.txt"));
        //part1(sampleInput);
        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_17_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Solution: "
            + part1(realInput));

        DEBUG = true;
        System.out.println("========= sample ==========");
        part2(sampleInput, 200);
        System.out.println("========= real ==========");
        part2(realInput, 10000);
        DEBUG = false;


        /* sample
        |....1..| will detect the loop

        notice it cleanly separates between the rocks:
        54 |..222..| 33
        53 |....1..| 32        <-------
        52 |...111.| 32
        51 |3...1..| 29, 32
        50 |30000..| 29, 31

        32 rocks makes it 53 high

        repeats again at rock 67 at 106 high:
        107 |..222..| 68
        106 |....1..| 67           <-------
        105 |...111.| 67
        104 |3...1..| 64, 67
        103 |30000..| 64, 66

        y delta    = 106-53 = 53
        rock delta = 67-32 = 35

        every 35 rocks we add 53 height

        1000000000000
        expected
        1514285714288

         */

        System.out.println("========= solving sample  =====");
        manuallySolvePart2(
            53, // firstLoopY
            32, // firstLoopRock
            106, // secondLoopY
            67, // secondLoopRock
            78 // leftOverY
        );
        System.out.println("Expected: " + 1514285714288L);


        /* real input
        |.....44| will detect the loop

        notice it cleanly separates between the rocks:
        2738 |...31..| 1767, 1769
        2737 |...111.| 1767
        2736 |....1..| 1767
        2735 |...0000| 1766
        2734 |.....44| 1765            <----------------------
        2733 |.3...44| 1764, 1765
        2732 |.3...2.| 1763, 1764

        5433 |...31..| 3502, 3504
        5432 |...111.| 3502
        5431 |....1..| 3502
        5430 |...0000| 3501
        5429 |.....44| 3500            <---------------------
        5428 |.3...44| 3499, 3500
        5427 |.3...2.| 3498, 3499
         */
        System.out.println("========= solving real  =====");
        manuallySolvePart2(
            2734, // firstLoopY
            1765, // firstLoopRock
            5429, // secondLoopY
            3500, // secondLoopRock
            2894 // leftOverY
        );
    }
}
