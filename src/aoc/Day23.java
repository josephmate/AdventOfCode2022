package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/23>Day 23: Unstable Diffusion</a>
 */
public class Day23 {

    private static boolean DEBUG = false;

    record Coord (int r, int c) {

    }

    private static Map<Coord, Character> parseInput(String input) {
        Map<Coord, Character> map = new HashMap<>();

        int r = 0;
        Iterator<String> lines = input.lines().iterator();
        while (lines.hasNext()) {
            String line = lines.next();

            for (int c = 0; c < line.length(); c++) {
                char ch = line.charAt(c);
                switch (ch) {
                    case '.' -> {}
                    case '#' -> map.put(new Coord(r,c), ch);
                    default -> throw new IllegalStateException("Unrecognized character"
                        + " r=" + r
                        + " c=" + c
                        + " ch=" + ch);
                }
            }

            r++;
        }

        return map;
    }

    private static boolean anyAdjacent(Coord coord, Map<Coord, Character> map) {
        for (int rDelta = -1; rDelta <= 1; rDelta++) {
            for (int cDelta = -1; cDelta <= 1; cDelta++) {
                if (rDelta != 0 || cDelta != 0) {
                    if (map.containsKey(new Coord(coord.r + rDelta, coord.c + cDelta))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     *     If there is no Elf in the N, NE, or NW adjacent positions, the Elf proposes moving north one step.
     *     If there is no Elf in the S, SE, or SW adjacent positions, the Elf proposes moving south one step.
     *     If there is no Elf in the W, NW, or SW adjacent positions, the Elf proposes moving west one step.
     *     If there is no Elf in the E, NE, or SE adjacent positions, the Elf proposes moving east one step.
     */
    private static boolean isDirectionOpen(Coord coord, char direction, Map<Coord, Character> map) {
        return switch(direction) {
            case 'N' -> !map.containsKey(new Coord(coord.r - 1, coord.c)) // N
                        && !map.containsKey(new Coord(coord.r - 1, coord.c + 1))  // NE
                        && !map.containsKey(new Coord(coord.r - 1, coord.c - 1)); // NW
            case 'S' -> !map.containsKey(new Coord(coord.r + 1, coord.c)) // S
                        && !map.containsKey(new Coord(coord.r + 1, coord.c + 1))  // SE
                        && !map.containsKey(new Coord(coord.r + 1, coord.c - 1)); // SW
            case 'W' -> !map.containsKey(new Coord(coord.r, coord.c - 1)) // W
                        && !map.containsKey(new Coord(coord.r - 1, coord.c - 1)) //NW
                        && !map.containsKey(new Coord(coord.r + 1, coord.c - 1)); //SW
            case 'E' -> !map.containsKey(new Coord(coord.r, coord.c + 1)) // E
                && !map.containsKey(new Coord(coord.r - 1, coord.c + 1)) //NE
                && !map.containsKey(new Coord(coord.r + 1, coord.c + 1)); //SE
            default -> throw new IllegalStateException();
        };
    }
    private static Coord moveOneStep(Coord coord, char direction) {
        return switch(direction) {
            case 'N' -> new Coord(coord.r -1, coord.c);
            case 'S' -> new Coord(coord.r + 1, coord.c);
            case 'W' -> new Coord(coord.r, coord.c - 1);
            case 'E' -> new Coord(coord.r, coord.c + 1);
            default -> throw new IllegalStateException();
        };
    }

    static void printMap(Map<Coord, Character> map) {
        if(!DEBUG) {
            return;
        }
        int minR = map.keySet().stream().mapToInt(Coord::r).min().orElseThrow();
        int maxR = map.keySet().stream().mapToInt(Coord::r).max().orElseThrow();
        int minC = map.keySet().stream().mapToInt(Coord::c).min().orElseThrow();
        int maxC = map.keySet().stream().mapToInt(Coord::c).max().orElseThrow();

        for (int r = minR; r <= maxR; r++) {
            for (int c = minC; c <= maxC; c++) {
                Character ch = map.get(new Coord(r,c));
                if (ch == null) {
                    System.out.print('.');
                } else {
                    System.out.print(ch);
                }
            }
            System.out.println();
        }
    }

    /**
     * This problem feels really similar to game of life.
     *
     * During the first half of each round, each Elf considers the eight positions adjacent to themself.
     * If no other Elves are in one of those eight positions,
     * the Elf does not do anything during this round. Otherwise,
     * the Elf looks in each of four directions in the following order and proposes moving one step in the first valid direction:
     *
     *     If there is no Elf in the N, NE, or NW adjacent positions, the Elf proposes moving north one step.
     *     If there is no Elf in the S, SE, or SW adjacent positions, the Elf proposes moving south one step.
     *     If there is no Elf in the W, NW, or SW adjacent positions, the Elf proposes moving west one step.
     *     If there is no Elf in the E, NE, or SE adjacent positions, the Elf proposes moving east one step.
     *
     * After each Elf has had a chance to propose a move, the second half of the round can begin.
     * Simultaneously, each Elf moves to their proposed destination tile if they were the only Elf to
     * propose moving to that position. If two or more Elves propose moving to the same position,
     * none of those Elves move.
     *
     * Finally, at the end of the round, the first direction the Elves considered is moved to the end
     * of the list of directions. For example, during the second round, the Elves would try proposing
     * a move to the south first, then west, then east, then north. On the third round, the Elves would
     * first consider west, then east, then north, then south.
     */
    private static long part1(String input) {
        Map<Coord, Character> map = parseInput(input);
        printMap(map);
        /*
         * Finally, at the end of the round, the first direction the Elves considered is moved to the end
         * of the list of directions. For example, during the second round, the Elves would try proposing
         * a move to the south first, then west, then east, then north. On the third round, the Elves would
         * first consider west, then east, then north, then south.
         */
        char[] directions = new char[]{'N', 'S', 'W', 'E'};


        for (int i = 0; i < 10; i++) {
            // make the plan
            Map<Coord, Character> plan = new HashMap<>();
            for (Coord elf : map.keySet()) {
                // If no other Elves are in one of those eight positions, the Elf does not do anything during this round.
                if (anyAdjacent(elf, map)) {
                    for (int j = 0; j < directions.length; j++) {
                        /*
                         * Finally, at the end of the round, the first direction the Elves considered is moved to the end
                         * of the list of directions. For example, during the second round, the Elves would try proposing
                         * a move to the south first, then west, then east, then north. On the third round, the Elves would
                         * first consider west, then east, then north, then south.
                         */
                        char direction = directions[(i + j) % directions.length];
                        if (isDirectionOpen(elf, direction, map)) {
                            plan.put(elf, direction);
                            break;
                        }
                    }
                }
            }

            // check for collisions
            //     destination    source
            Map<   Coord,         List<Coord>> collisionChecker = new HashMap<>();
            plan.forEach((source, direction) -> {
                Coord destination = moveOneStep(source, direction);
                List<Coord> colliders = collisionChecker.computeIfAbsent(destination, (coord) -> new ArrayList<>());
                colliders.add(source);
            });
            collisionChecker.forEach((destination, colliders) ->{
                if (colliders.size() > 1) {
                    for (Coord collider : colliders) {
                        plan.remove(collider);
                    }
                }
            });

            // execute the plan
            // move the elves that can
            Map<Coord, Character> nextMap = new HashMap<>();
            // add the elves that don't move
            map.keySet().stream()
                .filter(elf -> !plan.containsKey(elf))
                .forEach(elf -> nextMap.put(elf, '#'));
            plan.forEach((source, direction) -> {
                nextMap.put(moveOneStep(source, direction), '#');
            });
            map = nextMap;
            if (DEBUG) {
                System.out.println("======" + (i+1) + "=============");
            }
            printMap(map);
        }

        /*
         * Simulate the Elves' process and find the smallest rectangle that contains the
         * Elves after 10 rounds. How many empty ground tiles does that rectangle contain?
         */
        int minR = map.keySet().stream().mapToInt(Coord::r).min().orElseThrow();
        int maxR = map.keySet().stream().mapToInt(Coord::r).max().orElseThrow();
        int minC = map.keySet().stream().mapToInt(Coord::c).min().orElseThrow();
        int maxC = map.keySet().stream().mapToInt(Coord::c).max().orElseThrow();

        //     | ----------- size of rectangle ----|   number of elves
        if (DEBUG) {
            System.out.println(
                "maxR=" + maxR
                + " minR=" + minR
                + " maxR=" + maxC
                + " minR=" + minC
                + " (maxR - minR + 1)=" + (maxR - minR + 1)
                + " (maxC - minC + 1)=" + (maxC - minC + 1)
                + " map.size()=" +  map.size()
            );
        }
        return (maxR - minR + 1) * (maxC - minC + 1) - map.size();
    }

    private static long part2(String input) {
        Map<Coord, Character> map = parseInput(input);
        /*
         * Finally, at the end of the round, the first direction the Elves considered is moved to the end
         * of the list of directions. For example, during the second round, the Elves would try proposing
         * a move to the south first, then west, then east, then north. On the third round, the Elves would
         * first consider west, then east, then north, then south.
         */
        char[] directions = new char[]{'N', 'S', 'W', 'E'};

        int round = 0;
        while(true) {
            round++;
            // make the plan
            Map<Coord, Character> plan = new HashMap<>();
            for (Coord elf : map.keySet()) {
                // If no other Elves are in one of those eight positions, the Elf does not do anything during this round.
                if (anyAdjacent(elf, map)) {
                    for (int j = 0; j < directions.length; j++) {
                        /*
                         * Finally, at the end of the round, the first direction the Elves considered is moved to the end
                         * of the list of directions. For example, during the second round, the Elves would try proposing
                         * a move to the south first, then west, then east, then north. On the third round, the Elves would
                         * first consider west, then east, then north, then south.
                         */
                        char direction = directions[(round - 1 + j) % directions.length];
                        if (isDirectionOpen(elf, direction, map)) {
                            plan.put(elf, direction);
                            break;
                        }
                    }
                }
            }

            // check for collisions
            //     destination    source
            Map<   Coord,         List<Coord>> collisionChecker = new HashMap<>();
            plan.forEach((source, direction) -> {
                Coord destination = moveOneStep(source, direction);
                List<Coord> colliders = collisionChecker.computeIfAbsent(destination, (coord) -> new ArrayList<>());
                colliders.add(source);
            });
            collisionChecker.forEach((destination, colliders) ->{
                if (colliders.size() > 1) {
                    for (Coord collider : colliders) {
                        plan.remove(collider);
                    }
                }
            });

            // execute the plan
            // move the elves that can
            Map<Coord, Character> nextMap = new HashMap<>();
            // add the elves that don't move
            map.keySet().stream()
                .filter(elf -> !plan.containsKey(elf))
                .forEach(elf -> nextMap.put(elf, '#'));
            if (nextMap.size() == map.size()) {
                // no one moved;
                return round;
            }

            plan.forEach((source, direction) -> {
                nextMap.put(moveOneStep(source, direction), '#');
            });
            map = nextMap;
        }
    }

    public static void main(String[] args) throws IOException {
        int day = 23;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        DEBUG = true;

        part1(sampleInput);

        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Solution: "
            + part1(realInput));

        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part2_expected.txt")));
        System.out.println("Actual:   " +  part2(sampleInput));
        System.out.println("Solution: " +  part2(realInput));

    }
}
