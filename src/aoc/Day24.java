package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * --- Day 24: Blizzard Basin ---
 *
 * With everything replanted for next year (and with elephants and monkeys to tend the grove),
 * you and the Elves leave for the extraction point.
 *
 * Partway up the mountain that shields the grove is a flat, open area that serves as the
 * extraction point. It's a bit of a climb, but nothing the expedition can't handle.
 *
 * At least, that would normally be true; now that the mountain is covered in snow, things
 * have become more difficult than the Elves are used to.
 *
 * As the expedition reaches a valley that must be traversed to reach the extraction site,
 * you find that strong, turbulent winds are pushing small blizzards of snow and sharp ice
 * around the valley. It's a good thing everyone packed warm clothes! To make it across safely,
 * you'll need to find a way to avoid them.
 *
 * Fortunately, it's easy to see all of this from the entrance to the valley, so you make a map of
 * the valley and the blizzards (your puzzle input). For example:
 *
 * #.#####
 * #.....#
 * #>....#
 * #.....#
 * #...v.#
 * #.....#
 * #####.#
 *
 * The walls of the valley are drawn as #; everything else is ground. Clear ground -
 * where there is currently no blizzard - is drawn as .. Otherwise, blizzards are drawn
 * with an arrow indicating their direction of motion: up (^), down (v), left (<), or right (>).
 *
 * The above map includes two blizzards, one moving right (>) and one moving down (v).
 * In one minute, each blizzard moves one position in the direction it is pointing:
 *
 * #.#####
 * #.....#
 * #.>...#
 * #.....#
 * #.....#
 * #...v.#
 * #####.#
 *
 * Due to conservation of blizzard energy, as a blizzard reaches the wall of the valley,
 * a new blizzard forms on the opposite side of the valley moving in the same direction.
 * After another minute, the bottom downward-moving blizzard has been replaced with a new
 * downward-moving blizzard at the top of the valley instead:
 *
 * #.#####
 * #...v.#
 * #..>..#
 * #.....#
 * #.....#
 * #.....#
 * #####.#
 *
 * Because blizzards are made of tiny snowflakes, they pass right through each other.
 * After another minute, both blizzards temporarily occupy the same position, marked 2:
 *
 * #.#####
 * #.....#
 * #...2.#
 * #.....#
 * #.....#
 * #.....#
 * #####.#
 *
 * After another minute, the situation resolves itself, giving each blizzard back its personal
 * space:
 *
 * #.#####
 * #.....#
 * #....>#
 * #...v.#
 * #.....#
 * #.....#
 * #####.#
 *
 * Finally, after yet another minute, the rightward-facing blizzard on the right is replaced with
 * a new one on the left facing the same direction:
 *
 * #.#####
 * #.....#
 * #>....#
 * #.....#
 * #...v.#
 * #.....#
 * #####.#
 *
 * This process repeats at least as long as you are observing it, but probably forever.
 *
 * Here is a more complex example:
 *
 * #.######
 * #>>.<^<#
 * #.<..<<#
 * #>v.><>#
 * #<^v^^>#
 * ######.#
 *
 * Your expedition begins in the only non-wall position in the top row and needs to reach the only
 * non-wall position in the bottom row. On each minute, you can move up, down, left, or right,
 * or you can wait in place. You and the blizzards act simultaneously, and you cannot share a
 * position with a blizzard.
 *
 * In the above example, the fastest way to reach your goal requires 18 steps. Drawing the position
 * of the expedition as E, one way to achieve this is:
 *
 * Initial state:
 * #E######
 * #>>.<^<#
 * #.<..<<#
 * #>v.><>#
 * #<^v^^>#
 * ######.#
 *
 * Minute 1, move down:
 * #.######
 * #E>3.<.#
 * #<..<<.#
 * #>2.22.#
 * #>v..^<#
 * ######.#
 *
 * Minute 2, move down:
 * #.######
 * #.2>2..#
 * #E^22^<#
 * #.>2.^>#
 * #.>..<.#
 * ######.#
 *
 * Minute 3, wait:
 * #.######
 * #<^<22.#
 * #E2<.2.#
 * #><2>..#
 * #..><..#
 * ######.#
 *
 * Minute 4, move up:
 * #.######
 * #E<..22#
 * #<<.<..#
 * #<2.>>.#
 * #.^22^.#
 * ######.#
 *
 * Minute 5, move right:
 * #.######
 * #2Ev.<>#
 * #<.<..<#
 * #.^>^22#
 * #.2..2.#
 * ######.#
 *
 * Minute 6, move right:
 * #.######
 * #>2E<.<#
 * #.2v^2<#
 * #>..>2>#
 * #<....>#
 * ######.#
 *
 * Minute 7, move down:
 * #.######
 * #.22^2.#
 * #<vE<2.#
 * #>>v<>.#
 * #>....<#
 * ######.#
 *
 * Minute 8, move left:
 * #.######
 * #.<>2^.#
 * #.E<<.<#
 * #.22..>#
 * #.2v^2.#
 * ######.#
 *
 * Minute 9, move up:
 * #.######
 * #<E2>>.#
 * #.<<.<.#
 * #>2>2^.#
 * #.v><^.#
 * ######.#
 *
 * Minute 10, move right:
 * #.######
 * #.2E.>2#
 * #<2v2^.#
 * #<>.>2.#
 * #..<>..#
 * ######.#
 *
 * Minute 11, wait:
 * #.######
 * #2^E^2>#
 * #<v<.^<#
 * #..2.>2#
 * #.<..>.#
 * ######.#
 *
 * Minute 12, move down:
 * #.######
 * #>>.<^<#
 * #.<E.<<#
 * #>v.><>#
 * #<^v^^>#
 * ######.#
 *
 * Minute 13, move down:
 * #.######
 * #.>3.<.#
 * #<..<<.#
 * #>2E22.#
 * #>v..^<#
 * ######.#
 *
 * Minute 14, move right:
 * #.######
 * #.2>2..#
 * #.^22^<#
 * #.>2E^>#
 * #.>..<.#
 * ######.#
 *
 * Minute 15, move right:
 * #.######
 * #<^<22.#
 * #.2<.2.#
 * #><2>E.#
 * #..><..#
 * ######.#
 *
 * Minute 16, move right:
 * #.######
 * #.<..22#
 * #<<.<..#
 * #<2.>>E#
 * #.^22^.#
 * ######.#
 *
 * Minute 17, move down:
 * #.######
 * #2.v.<>#
 * #<.<..<#
 * #.^>^22#
 * #.2..2E#
 * ######.#
 *
 * Minute 18, move down:
 * #.######
 * #>2.<.<#
 * #.2v^2<#
 * #>..>2>#
 * #<....>#
 * ######E#
 *
 * What is the fewest number of minutes required to avoid the blizzards and reach the goal?
 *
 * That's not the right answer; your answer is too high. If you're stuck, make sure you're using
 * the full input data; there are also some general tips on the about page, or you can ask for hints
 * on the subreddit. Please wait one minute before trying again. (You guessed 396.)
 *
 * my objective function did not find a min
 *
 *
 * --- Part Two ---
 *
 * As the expedition reaches the far side of the valley, one of the Elves looks especially dismayed:
 *
 * He forgot his snacks at the entrance to the valley!
 *
 * Since you're so good at dodging blizzards, the Elves humbly request that you go back for
 * his snacks. From the same initial conditions, how quickly can you make it from the start to the
 * goal, then back to the start, then back to the goal?
 *
 * In the above example, the first trip to the goal takes 18 minutes, the trip back to the start
 * takes 23 minutes, and the trip back to the goal again takes 13 minutes, for a total time of 54 minutes.
 *
 * What is the fewest number of minutes required to reach the goal, go back to the start,
 * then reach the goal again?
 */
public class Day24 {

    private static boolean DEBUG = false;


    private static List<List<List<Character>>> parseInput(String input) {
        List<String> lines = input.lines().collect(Collectors.toList());
        List<List<List<Character>>> result = new ArrayList<>();

        for (int r = 0; r < lines.size(); r++) {
            List<List<Character>> row = new ArrayList<>();
            for (int c = 0; c < lines.get(r).length(); c++) {
                List<Character> windDirections = new ArrayList<>(4);
                char windDirection = lines.get(r).charAt(c);
                windDirections.add(windDirection);
                row.add(windDirections);
            }
            result.add(row);
        }

        return result;
    }

    /*
     * The walls of the valley are drawn as #; everything else is ground. Clear ground -
     * where there is currently no blizzard - is drawn as .. Otherwise, blizzards are drawn
     * with an arrow indicating their direction of motion: up (^), down (v), left (<), or right (>).
     *
     * Due to conservation of blizzard energy, as a blizzard reaches the wall of the valley,
     * a new blizzard forms on the opposite side of the valley moving in the same direction.
     */
    private static List<List<List<Character>>> calcNextWind(List<List<List<Character>>> map) {
        List<List<List<Character>>> next = new ArrayList<>();
        int R = map.size();
        int C = map.get(0).size();

        // copy the #'s and initialize the nested arrays
        for (int r = 0; r < map.size(); r++) {
            List<List<Character>> sourceRow = map.get(r);
            List<List<Character>> row = new ArrayList<>();
            for (int c = 0; c < map.get(0).size(); c++) {
                List<Character> col = new ArrayList<>();
                if (sourceRow.get(c).get(0) == '#') {
                    col.add('#');
                }
                row.add(col);
            }
            next.add(row);
        }

        // move the wind
        for (int r = 0; r < map.size(); r++) {
            List<List<Character>> row = map.get(r);
            for (int c = 0; c < map.get(0).size(); c++) {
                List<Character> col = row.get(c);
                for(char ch : col) {
                    switch (ch) {
                        case '^' -> {
                            int newR = r - 1;
                            if (newR == 0) {
                                // hit a wall need to loop around
                                newR = R - 2;
                            }
                            next.get(newR).get(c).add('^');
                        }
                        case 'v' -> {
                            int newR = r + 1;
                            if (newR == R - 1) {
                                // hit a wall need to loop around
                                newR = 1;
                            }
                            next.get(newR).get(c).add('v');
                        }
                        case '<' -> {
                            int newC = c - 1;
                            if (newC == 0) {
                                // hit a wall need to loop around
                                newC = C - 2;
                            }
                            next.get(r).get(newC).add('<');
                        }
                        case '>' -> {
                            int newC = c + 1;
                            if (newC == C - 1) {
                                // hit a wall need to loop around
                                newC = 1;
                            }
                            next.get(r).get(newC).add('>');
                        }
                        default -> {} // nothing to do
                    }
                }
            }
        }

        // fill in any empties with dots
        for (int r = 0; r < next.size(); r++) {
            List<List<Character>> row = next.get(r);
            for (int c = 0; c < next.get(0).size(); c++) {
                List<Character> col = row.get(c);
                if (col.isEmpty()) {
                    col.add('.');
                }
            }
        }

        return next;
    }

    private static void printMap( List<List<List<Character>>> map) {
        int R = map.size();
        int C = map.get(0).size();
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                List<Character> col = map.get(r).get(c);
                if (col.size() == 1) {
                    System.out.print(col.get(0));
                } else {
                    System.out.print(col.size());
                }

            }
            System.out.println();
        }
    }


    record Coord(int r, int c) {

    }

    record JourneySoFar(int distance, int minute, Coord coord, TrekStatus trekStatus) {

    }

    record DistanceInfo (Coord current, Coord initial, Coord goal, TrekStatus trekStatus) {

    }

    record ExitConditionInfo (Coord current, Coord goal, TrekStatus trekStatus) {

    }


    private static int manDist(Coord a, Coord b) {
        return Math.max(a.r, b.r) - Math.min(a.r, b.r)
            + Math.max(a.c, b.c) - Math.min(a.c, b.c);
    }

    /**
     * Your expedition begins in the only non-wall position in the top row and needs to reach the only
     * non-wall position in the bottom row. On each minute, you can move up, down, left, or right,
     * or you can wait in place. You and the blizzards act simultaneously, and you cannot share a
     * position with a blizzard.
     */
    private static List<Coord> generateMoves(Coord coord) {
        return List.of(
            coord, // wait in place
            new Coord(coord.r - 1, coord.c), // up
            new Coord(coord.r + 1, coord.c), // down
            new Coord(coord.r, coord.c - 1), // left
            new Coord(coord.r, coord.c + 1) // right
        );
    }

    enum TrekStatus {
        FIRST_TREK,
        RETURNING_FOR_SNACK,
        GOT_SNACK
    }

    private static Optional<Long> solve(
        List<List<List<Character>>> initialMap,
        List<List<List<List<Character>>>> maps,
        int maxMinute,
        Function<DistanceInfo, Integer> distanceFunction,
        Function<ExitConditionInfo, Boolean> exitCondition
    ) {
        int R = initialMap.size();
        int C = initialMap.get(0).size();

        // determine starting Coord
        Coord start = null;
        for (int c = 0; c < C; c++) {
            if (initialMap.get(0).get(c).get(0) == '.') {
                start = new Coord(0, c);
                break;
            }
        }
        // determine goal
        Coord goalNonFinal = null;
        for (int c = 0; c < C; c++) {
            if (initialMap.get(R-1).get(c).get(0) == '.') {
                goalNonFinal = new Coord(R-1, c);
                break;
            }
        }
        final Coord goal = goalNonFinal;


        // A* search

        //         visited add that time already
        // to reduce the search space
        Map<Coord, Map<TrekStatus, Set<Integer>>> visited = new HashMap();
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                Map<TrekStatus, Set<Integer>> statuses = new HashMap<>();
                for (TrekStatus status : TrekStatus.values()) {
                    statuses.put(status, new HashSet<>());
                }
                visited.put(new Coord(r,c), statuses);
            }
        }

        PriorityQueue<JourneySoFar> priorityQueue = new PriorityQueue<>(
            Comparator.comparingInt((JourneySoFar a) -> a.distance)
                .thenComparingInt(a -> a.minute)
        );
        visited.get(start).get(TrekStatus.FIRST_TREK).add(0);
        priorityQueue.add(new JourneySoFar(manDist(start, goal), 0, start, TrekStatus.FIRST_TREK));

        while (!priorityQueue.isEmpty()) {
            JourneySoFar journeySoFar = priorityQueue.remove();
            int nextMin = journeySoFar.minute + 1;
            if (nextMin > maxMinute) {
                continue;
            }
            List<List<List<Character>>> destinationMap = maps.get(nextMin);

            List<Coord> potentials = generateMoves(journeySoFar.coord).stream()
                .filter(coord -> coord.r >= 0)
                .filter(coord -> coord.r < R)
                .filter(coord -> coord.c >= 0)
                .filter(coord -> coord.c < C)
                .filter(coord -> destinationMap.get(coord.r).get(coord.c).get(0) == '.')
                .filter(coord -> !visited.get(coord).get(journeySoFar.trekStatus).contains(nextMin))
                .collect(Collectors.toList());

            for (Coord potential : potentials) {
                if (exitCondition.apply(new ExitConditionInfo(potential, goal, journeySoFar.trekStatus))) {
                    return Optional.of((long)nextMin);
                }
                visited.get(potential).get(journeySoFar.trekStatus).add(nextMin);

                 final TrekStatus newTrekStatus;
                 if (journeySoFar.trekStatus == TrekStatus.FIRST_TREK && potential.equals(goal)) {
                     newTrekStatus = TrekStatus.RETURNING_FOR_SNACK;
                 } else if (journeySoFar.trekStatus == TrekStatus.RETURNING_FOR_SNACK && potential.equals(start)) {
                     newTrekStatus = TrekStatus.GOT_SNACK;
                 } else {
                     newTrekStatus = journeySoFar.trekStatus;
                 }


                JourneySoFar nextJourney = new JourneySoFar(
                    distanceFunction.apply(new DistanceInfo(potential, start, goal, newTrekStatus)),
                    nextMin,
                    potential,
                    newTrekStatus);

                priorityQueue.add(nextJourney);
            }

        }

        return Optional.empty();
    }

    private static long solve(
        String input,
        Function<DistanceInfo, Integer> distanceFunction,
        Function<ExitConditionInfo, Boolean> exitCondition
    ) {
        List<List<List<Character>>> initialMap = parseInput(input);

        // pre-compute wind positions
        int PRE_COMPUTE_LEN = 1000;
        // maps -> rows -> cols -> wind directions
        List<List<List<List<Character>>>> maps = new ArrayList<>();
        maps.add(initialMap);
        for (int i = 1; i < PRE_COMPUTE_LEN; i++) {
            maps.add(calcNextWind(maps.get(i-1)));
        }

        long lastSolution = Integer.MAX_VALUE;
        long lowerBound = 1;
        long upperbound = PRE_COMPUTE_LEN;
        while (lowerBound < upperbound) {
            long midPoint = (lowerBound + upperbound) / 2;
            Optional<Long> potentialSolution = solve(initialMap, maps, (int)midPoint, distanceFunction, exitCondition);
            if (potentialSolution.isPresent()) {
                lastSolution = Math.min(lastSolution, potentialSolution.get());
                System.out.println(lastSolution);
                upperbound = potentialSolution.get() - 1;
            } else {
                lowerBound = midPoint + 1;
            }
        }

        return lastSolution;
    }

    private static long part1(String input) {
        return solve(input,
            (distanceInfo) -> manDist(distanceInfo.current, distanceInfo.goal),
            (exitConditionInfo) -> exitConditionInfo.current.equals(exitConditionInfo.goal)
        );
    }

    private static long part2(String input) {
        return solve(input,
            (distanceInfo) -> switch (distanceInfo.trekStatus) {
                case FIRST_TREK -> manDist(distanceInfo.initial, distanceInfo.goal) * 2 + manDist(distanceInfo.current, distanceInfo.goal);
                case RETURNING_FOR_SNACK -> manDist(distanceInfo.initial, distanceInfo.goal) + manDist(distanceInfo.current, distanceInfo.initial);
                case GOT_SNACK -> manDist(distanceInfo.current, distanceInfo.goal);
            },
            (exitConditionInfo) -> exitConditionInfo.current.equals(exitConditionInfo.goal)
                && exitConditionInfo.trekStatus == TrekStatus.GOT_SNACK
        );
    }

    public static void main(String[] args) throws IOException {
        int day = 24;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        String windTestSample = """
            #.#####
            #.....#
            #..^..#
            #.<.>.#
            #..v..#
            #.....#
            #####.#
            """;
        List<List<List<Character>>> windTestSampleMap = parseInput(windTestSample);
        System.out.println("========== 0 ==========");
        printMap(windTestSampleMap);
        windTestSampleMap = calcNextWind(windTestSampleMap);
        System.out.println("========== 1 ==========");
        printMap(windTestSampleMap);
        windTestSampleMap = calcNextWind(windTestSampleMap);
        System.out.println("========== 2 ==========");
        printMap(windTestSampleMap);
        windTestSampleMap = calcNextWind(windTestSampleMap);
        System.out.println("========== 3 ==========");
        printMap(windTestSampleMap);
        windTestSampleMap = calcNextWind(windTestSampleMap);
        System.out.println("========== 4 ==========");
        printMap(windTestSampleMap);

        DEBUG = true;

//        part1(sampleInput);

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
