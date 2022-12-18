package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * --- Day 17: Pyroclastic Flow ---
 *
 * Your handheld device has located an alternative exit from the cave for you and the elephants.
 * The ground is rumbling almost continuously now, but the strange valves bought you some time.
 * It's definitely getting warmer in here, though.
 *
 * The tunnels eventually open into a very tall, narrow chamber. Large, oddly-shaped rocks are
 * falling into the chamber from above, presumably due to all the rumbling. If you can't work out
 * where the rocks will fall next, you might be crushed!
 *
 * The five types of rocks have the following peculiar shapes, where # is rock and . is empty space:
 *
 * ####
 *
 * .#.
 * ###
 * .#.
 *
 * ..#
 * ..#
 * ###
 *
 * #
 * #
 * #
 * #
 *
 * ##
 * ##
 *
 * The rocks fall in the order shown above: first the - shape, then the + shape, and so on. Once
 * the end of the list is reached, the same order repeats: the - shape falls first, sixth, 11th,
 * 16th, etc.
 *
 * The rocks don't spin, but they do get pushed around by jets of hot gas coming out of the walls
 * themselves. A quick scan reveals the effect the jets of hot gas will have on the rocks as they
 * fall (your puzzle input).
 *
 * For example, suppose this was the jet pattern in your cave:
 *
 * >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
 *
 * In jet patterns, < means a push to the left, while > means a push to the right. The pattern
 * above means that the jets will push a falling rock right, then right, then right, then left,
 * then left, then right, and so on. If the end of the list is reached, it repeats.
 *
 * The tall, vertical chamber is exactly seven units wide. Each rock appears so that its left
 * edge is two units away from the left wall and its bottom edge is three units above the highest
 * rock in the room (or the floor, if there isn't one).
 *
 * After a rock appears, it alternates between being pushed by a jet of hot gas one unit
 * (in the direction indicated by the next symbol in the jet pattern) and then falling one unit
 * down. If any movement would cause any part of the rock to move into the walls, floor, or a
 * stopped rock, the movement instead does not occur. If a downward movement would have caused a
 * falling rock to move into the floor or an already-fallen rock, the falling rock stops where
 * it is (having landed on something) and a new rock immediately begins falling.
 *
 * Drawing falling rocks with @ and stopped rocks with #, the jet pattern in the example
 * above manifests as follows:
 *
 * The first rock begins falling:
 * |..@@@@.|
 * |.......|
 * |.......|
 * |.......|
 * +-------+
 *
 * Jet of gas pushes rock right:
 * |...@@@@|
 * |.......|
 * |.......|
 * |.......|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@@@@|
 * |.......|
 * |.......|
 * +-------+
 *
 * Jet of gas pushes rock right, but nothing happens:
 * |...@@@@|
 * |.......|
 * |.......|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@@@@|
 * |.......|
 * +-------+
 *
 * Jet of gas pushes rock right, but nothing happens:
 * |...@@@@|
 * |.......|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@@@@|
 * +-------+
 *
 * Jet of gas pushes rock left:
 * |..@@@@.|
 * +-------+
 *
 * Rock falls 1 unit, causing it to come to rest:
 * |..####.|
 * +-------+
 *
 * A new rock begins falling:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock left:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |.......|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock right:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock left:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |.......|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit:
 * |..@....|
 * |.@@@...|
 * |..@....|
 * |..####.|
 * +-------+
 *
 * Jet of gas pushes rock right:
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |..####.|
 * +-------+
 *
 * Rock falls 1 unit, causing it to come to rest:
 * |...#...|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * A new rock begins falling:
 * |....@..|
 * |....@..|
 * |..@@@..|
 * |.......|
 * |.......|
 * |.......|
 * |...#...|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * The moment each of the next few rocks begins falling,
 * you would see this:
 *
 * |..@....|
 * |..@....|
 * |..@....|
 * |..@....|
 * |.......|
 * |.......|
 * |.......|
 * |..#....|
 * |..#....|
 * |####...|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@...|
 * |..@@...|
 * |.......|
 * |.......|
 * |.......|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@@@.|
 * |.......|
 * |.......|
 * |.......|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |...@...|
 * |..@@@..|
 * |...@...|
 * |.......|
 * |.......|
 * |.......|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |....@..|
 * |....@..|
 * |..@@@..|
 * |.......|
 * |.......|
 * |.......|
 * |..#....|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@....|
 * |..@....|
 * |..@....|
 * |..@....|
 * |.......|
 * |.......|
 * |.......|
 * |.....#.|
 * |.....#.|
 * |..####.|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@...|
 * |..@@...|
 * |.......|
 * |.......|
 * |.......|
 * |....#..|
 * |....#..|
 * |....##.|
 * |....##.|
 * |..####.|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * |..@@@@.|
 * |.......|
 * |.......|
 * |.......|
 * |....#..|
 * |....#..|
 * |....##.|
 * |##..##.|
 * |######.|
 * |.###...|
 * |..#....|
 * |.####..|
 * |....##.|
 * |....##.|
 * |....#..|
 * |..#.#..|
 * |..#.#..|
 * |#####..|
 * |..###..|
 * |...#...|
 * |..####.|
 * +-------+
 *
 * To prove to the elephants your simulation is accurate, they want to know how tall the tower
 * will get after 2022 rocks have stopped (but before the 2023rd rock begins falling).
 * In this example, the tower of rocks will be 3068 units tall.
 *
 * How many units tall will the tower of rocks be after 2022 rocks have stopped falling?
 *
 * --- Part Two ---
 *
 * The elephants are not impressed by your simulation. They demand to know how tall the tower will
 * be after 1000000000000 rocks have stopped! Only then will they feel confident enough to proceed
 * through the cave.
 *
 * In the example above, the tower would be 1514285714288 units tall!
 *
 * How tall will the tower be after 1000000000000 rocks have stopped?
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



    private static long part2(String input) {
        final String jets = input.trim();

        Set<Coord> rocks = new HashSet<>();
        // TODO: option 1 find two adjacent lines with with a rock in at least 1 x
        //       then remove all rocks below it

        // TODO: option 2 detect a repeating structure???

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
        for (long rock = 0; rock < 1000000000000L; rock++) {
            // and a new rock immediately begins falling.
            Collidable currentRock = rockMakers.get( (int)(rock% rockMakers.size()) ).start(highestRock, LEFT_WALL, RIGHT_WALL);

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
                char jetDirection = jets.charAt( (int)(jet % jets.length()) );
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

    public static void main(String[] args) throws IOException {

        DEBUG = true;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_17_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_17.txt"));
        part1(sampleInput);
        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_17_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Solution: "
            + part1(realInput));

        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_17_sample_part2_expected.txt")));
        System.out.println("Actual:   " +  part2(sampleInput));
        System.out.println("Solution: " +  part2(realInput));

    }
}
