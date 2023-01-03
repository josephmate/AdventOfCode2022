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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/19>Day 19: Not Enough Minerals</a>
 */
public class Day19 {

    private static boolean DEBUG = false;

    record Blueprint(
        int id,
        int oreRobotOreCost,
        int clayRobotOreCost,
        int obsidianRobotOreCost,
        int obsidianRobotClayCost,
        int geodeRobotOreCost,
        int geodeRobotObsidianCost
    ) {

    }

    /**
     * <pre>
     * Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 12 clay. Each geode robot costs 4 ore and 19 obsidian.
     * </pre>
     * <pre>
     * Blueprint 1:
     * Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 12 clay. Each geode robot costs 4 ore and 19 obsidian.
     * </pre>
     * <pre>
     * Each ore robot costs 4 ore
     * Each clay robot costs 4 ore
     * Each obsidian robot costs 4 ore and 12 clay
     * Each geode robot costs 4 ore and 19 obsidian.
     * </pre>
     * @param line
     * @return
     */
    private static Blueprint parseLine(String line) {
        String [] firstSplit = line.split(": ");
        int id = Integer.parseInt(firstSplit[0].split(" ")[1]);
        String [] secondSplit = firstSplit[1].split("\\. ");
        // Each ore robot costs 4 ore
        //  0    1   2     3    4  5
        int i = 0;
        String [] oreRobotSplit = secondSplit[i++].split(" ");
        int oreRobotOreCost = Integer.parseInt(oreRobotSplit[4]);

        // Each clay robot costs 4 ore
        //  0    1   2     3    4  5
        String [] clayRobotSplit = secondSplit[i++].split(" ");
        int clayRobotOreCost = Integer.parseInt(clayRobotSplit[4]);

        // Each obsidian robot costs 4 ore and 12 clay
        //  0    1         2     3   4  5   6  7   8
        String [] obsidianRobotSplit = secondSplit[i++].split(" ");
        int obsidianRobotOreCost = Integer.parseInt(obsidianRobotSplit[4]);
        int obsidianRobotClayCost = Integer.parseInt(obsidianRobotSplit[7]);

        // Each geode robot costs 4 ore and 19 obsidian.
        //  0    1      2     3   4  5   6  7   8
        String [] geodeRobotSplit = secondSplit[i++].split(" ");
        int geodeRobotOreCost = Integer.parseInt(geodeRobotSplit[4]);
        int geodeRobotClayCost = Integer.parseInt(geodeRobotSplit[7]);

        return new Blueprint(
            id,
            oreRobotOreCost,
            clayRobotOreCost,
            obsidianRobotOreCost,
            obsidianRobotClayCost,
            geodeRobotOreCost,
            geodeRobotClayCost
        );
    }

    private static List<Blueprint> parseInput(String input) {
        return input.lines()
            .map(Day19::parseLine)
            .collect(Collectors.toList());
    }

    record SearchIteration(
        int minute,
        int ore,
        int clay,
        int obsidian,
        int geode,
        int oreRobots,
        int clayRobots,
        int obsidianRobots,
        int geodeRobots
    ) {

    }

    private static Optional<SearchIteration> buyOreBot(
        SearchIteration current, Blueprint blueprint, int minutesAvailable
    ) {
        if (current.oreRobots >= 7) {
            return Optional.empty();
        }
        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= minutesAvailable-2; i++) {
            if (oreSoFar >= blueprint.oreRobotOreCost) {
                oreSoFar = oreSoFar - blueprint.oreRobotOreCost;
                return Optional.of(new SearchIteration(
                    i+1,//int minute,
                    oreSoFar + current.oreRobots, //int ore,
                    claySoFar +  current.clayRobots, //int clay,
                    obsidianSoFar + current.obsidianRobots, //int obsidian,
                    geodeSoFar + current.geodeRobots, //int geode,
                    current.oreRobots + 1, //int oreRobots,
                    current.clayRobots, //int clayRobots,
                    current.obsidianRobots, //int obsidianRobots,
                    current.geodeRobots  //int geodeRobots
                ));
            }
            oreSoFar += current.oreRobots;
            claySoFar += current.clayRobots;
            obsidianSoFar += current.obsidianRobots;
            geodeSoFar += current.geodeRobots;
        }
        return Optional.empty();
    }

    private static Optional<SearchIteration> buyClayBot(
        SearchIteration current, Blueprint blueprint, int minutesAvailable
    ) {
        if (current.clayRobots >= 10) {
            return Optional.empty();
        }
        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= minutesAvailable-2; i++) {
            if (oreSoFar >= blueprint.clayRobotOreCost) {
                oreSoFar = oreSoFar - blueprint.clayRobotOreCost;
                return Optional.of(new SearchIteration(
                    i+1,//int minute,
                    oreSoFar + current.oreRobots, //int ore,
                    claySoFar +  current.clayRobots, //int clay,
                    obsidianSoFar + current.obsidianRobots, //int obsidian,
                    geodeSoFar + current.geodeRobots, //int geode,
                    current.oreRobots, //int oreRobots,
                    current.clayRobots + 1, //int clayRobots,
                    current.obsidianRobots, //int obsidianRobots,
                    current.geodeRobots  //int geodeRobots
                ));
            }
            oreSoFar += current.oreRobots;
            claySoFar += current.clayRobots;
            obsidianSoFar += current.obsidianRobots;
            geodeSoFar += current.geodeRobots;
        }
        return Optional.empty();
    }

    private static Optional<SearchIteration> buyObsidianBot(
        SearchIteration current, Blueprint blueprint, int minutesAvailable
    ) {
        if (current.clayRobots == 0) {
            return Optional.empty();
        }

        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= minutesAvailable-2; i++) {
            if (oreSoFar >= blueprint.obsidianRobotOreCost
                && claySoFar >= blueprint.obsidianRobotClayCost
            ) {
                oreSoFar = oreSoFar - blueprint.obsidianRobotOreCost;
                claySoFar = claySoFar - blueprint.obsidianRobotClayCost;
                return Optional.of(new SearchIteration(
                    i+1,//int minute,
                    oreSoFar + current.oreRobots, //int ore,
                    claySoFar +  current.clayRobots, //int clay,
                    obsidianSoFar + current.obsidianRobots, //int obsidian,
                    geodeSoFar + current.geodeRobots, //int geode,
                    current.oreRobots, //int oreRobots,
                    current.clayRobots, //int clayRobots,
                    current.obsidianRobots + 1, //int obsidianRobots,
                    current.geodeRobots  //int geodeRobots
                ));
            }
            oreSoFar += current.oreRobots;
            claySoFar += current.clayRobots;
            obsidianSoFar += current.obsidianRobots;
            geodeSoFar += current.geodeRobots;
        }
        return Optional.empty();
    }

    private static Optional<SearchIteration> buyGeodeBot(
        SearchIteration current, Blueprint blueprint, int minutesAvailable
    ) {
        if (current.obsidianRobots == 0) {
            return Optional.empty();
        }

        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= minutesAvailable-2; i++) {
            if (oreSoFar >= blueprint.geodeRobotOreCost
                && obsidianSoFar >= blueprint.geodeRobotObsidianCost
            ) {
                oreSoFar = oreSoFar - blueprint.geodeRobotOreCost;
                obsidianSoFar = obsidianSoFar - blueprint.geodeRobotObsidianCost;
                return Optional.of(new SearchIteration(
                    i+1,//int minute,
                    oreSoFar + current.oreRobots, //int ore,
                    claySoFar +  current.clayRobots, //int clay,
                    obsidianSoFar + current.obsidianRobots, //int obsidian,
                    geodeSoFar + current.geodeRobots, //int geode,
                    current.oreRobots, //int oreRobots,
                    current.clayRobots, //int clayRobots,
                    current.obsidianRobots, //int obsidianRobots,
                    current.geodeRobots + 1  //int geodeRobots
                ));
            }
            oreSoFar += current.oreRobots;
            claySoFar += current.clayRobots;
            obsidianSoFar += current.obsidianRobots;
            geodeSoFar += current.geodeRobots;
        }
        return Optional.empty();
    }

    /**
     * 5 possible moves over 24 minutes:
     * 5^24 possibilities
     *
     * How can we reduce the search space?
     * Option 1: Start from the goal instead?
     *   - decided against this because you still have the same problem, just in reverse
     *
     * Option 2 : Reduce search space
     * - tried with the map of minute to score
     *   - got complicated
     *     - how do you pick the score?
     *     - how close to the best so far?
     * - don't move one minute at a time
     *   - always gonna buy/build a robot so just do that directly
     *     don't
     *     - only 4 options now: ore, clay, obsidian, geode bot
     *     - need at least 2 minutes to save enough to buy (at least 2 ores needed)
     *     - 4^12
     *     - comparing bits
     *       - log(5^24)/log(2) = 55
     *       - log(4^12)/log(2) = 24
     *
     * @param current
     * @param blueprint
     * @return
     */
    private static List<SearchIteration> generateMoves(SearchIteration current, Blueprint blueprint, int minutesAvailable) {
        return List.of(
                buyOreBot(current,blueprint, minutesAvailable),
                buyClayBot(current,blueprint, minutesAvailable),
                buyObsidianBot(current,blueprint, minutesAvailable),
                buyGeodeBot(current,blueprint, minutesAvailable)
            )
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            //.map(noResourceYet -> applyOre(current, noResourceYet))
            .collect(Collectors.toList());
    }

    /**
     * Whoops. misread part 1. though I could build as much as I wanted
     */
    private static List<SearchIteration> generateMovesOld(SearchIteration current, Blueprint blueprint) {
        List<SearchIteration> result = new ArrayList<>();

        int nextMinute = current.minute + 1;

        for (int numOreRobots = 0;
             numOreRobots <= current.ore/blueprint.oreRobotOreCost;
             numOreRobots++
        ) {
            int oreAfterOreRobots = current.ore - (numOreRobots*blueprint.oreRobotOreCost);
            for(int numClayRobots = 0;
                numClayRobots <= oreAfterOreRobots/blueprint.clayRobotOreCost;
                numClayRobots++
            ) {
                int oreAfterClayRobots = oreAfterOreRobots - (numClayRobots*blueprint.clayRobotOreCost);
                for (int numObsidianRobots = 0;
                     numObsidianRobots <= Math.min(oreAfterClayRobots/blueprint.obsidianRobotOreCost, current.clay/blueprint.obsidianRobotClayCost);
                     numObsidianRobots++
                ) {
                    int oreAfterObsidianRobots = oreAfterClayRobots - (numObsidianRobots*blueprint.obsidianRobotOreCost);
                    int clayAfterObsidianRobots = current.clay - (numObsidianRobots*blueprint.obsidianRobotClayCost);
                    for (int numGeodeRobots = 0;
                         numGeodeRobots <= Math.min(oreAfterObsidianRobots/blueprint.geodeRobotOreCost, current.obsidian/blueprint.geodeRobotObsidianCost);
                         numGeodeRobots++
                    ) {
                        int oreAfterGeodeRobots = oreAfterObsidianRobots - (numGeodeRobots* blueprint.geodeRobotOreCost);
                        int obsidianAfterGeodeRobots = current.obsidian - (numGeodeRobots*blueprint.geodeRobotObsidianCost);
                        result.add(new SearchIteration(
                            nextMinute,//int minute,
                            oreAfterGeodeRobots, //int ore,
                            clayAfterObsidianRobots, //int clay,
                            obsidianAfterGeodeRobots, //int obsidian,
                            current.geode, //int geode,
                            current.oreRobots + numOreRobots, //int oreRobots,
                            current.clayRobots + numClayRobots, //int clayRobots,
                            current.obsidianRobots + numObsidianRobots, //int obsidianRobots,
                            current.geodeRobots + numGeodeRobots  //int geodeRobots
                        ));
                    }
                }
            }
        }

        return result;
    }

    private static SearchIteration applyOre(
        SearchIteration current,
        SearchIteration moveWithoutOre
    ) {
        int timePassed = moveWithoutOre.minute - current.minute;

        return new SearchIteration(
            moveWithoutOre.minute,//int minute,
            moveWithoutOre.ore      + (current.oreRobots     *timePassed), //int ore,
            moveWithoutOre.clay     + (current.clayRobots    *timePassed), //int clay,
            moveWithoutOre.obsidian + (current.obsidianRobots*timePassed), //int obsidian,
            moveWithoutOre.geode    + (current.geodeRobots   *timePassed), //int geode,
            moveWithoutOre.oreRobots, //int oreRobots,
            moveWithoutOre.clayRobots, //int clayRobots,
            moveWithoutOre.obsidianRobots, //int obsidianRobots,
            moveWithoutOre.geodeRobots  //int geodeRobots
        );
    }

    private static int score(SearchIteration move, Blueprint blueprint, int minutesAvailable) {
        return
//            2 * (
//                move.ore
//                + move.oreRobots*blueprint.oreRobotOreCost
//                + move.clayRobots*blueprint.clayRobotOreCost
//                + move.obsidianRobots*blueprint.obsidianRobotOreCost
//                + move.oreRobots*(minutesAvailable-move.minute)
//            )
//            + 1 * (
//                move.clay
//                + move.obsidianRobots * blueprint.obsidianRobotClayCost
//                + move.clay*(minutesAvailable-move.minute)
//            )
//            + 10 * (
//                move.obsidian
//                + move.geodeRobots * blueprint.geodeRobotObsidianCost
//                + move.obsidianRobots*(minutesAvailable-move.minute)
//            )
//            + 100 * (
//                move.geode
//                + move.geodeRobots*(minutesAvailable-move.minute)
//            )
              move.geode
                  + move.geodeRobots*(minutesAvailable-move.minute)
            ;
    }

    private static int qualityLevel(
        Blueprint blueprint,
        int minutesAvailable,
        int [] percentMap
    ) {

        long t1 = System.currentTimeMillis();
        int maxSoFar = 0;
        SearchIteration best = null;

        // need to be at least the best score so far
        Set<SearchIteration> visited = new HashSet<>();
        Map<Integer, Integer> scorePruner = new HashMap<>();

        PriorityQueue<SearchIteration> priorityQueue = new PriorityQueue<>(
            // starting out with BFS for now since I can come up with a good cost function
            Comparator.comparingInt(move -> score(move, blueprint, minutesAvailable))
        );
        // Fortunately, you have exactly one ore-collecting robot in your pack that you can use to kickstart the whole operation.
        SearchIteration start = new SearchIteration(
            0,//int minute,
            0, //int ore,
            0, //int clay,
            0, //int obsidian,
            0, //int geode,
            1, //int oreRobots,
            0, //int clayRobots,
            0, //int obsidianRobots,
            0  //int geodeRobots
        );
        scorePruner.put(0, score(start, blueprint, minutesAvailable));
        priorityQueue.add(start);

        while (!priorityQueue.isEmpty()) {
            SearchIteration current = priorityQueue.remove();
            int currentMoveScore = score(current, blueprint, minutesAvailable);
            int currentBestScoreSoFar = scorePruner.getOrDefault(current.minute, 0);
            if (current.minute == minutesAvailable) {
                if (current.geode > maxSoFar) {
                    maxSoFar = current.geode;
                    best = current;
                }
            } else if (currentMoveScore >= (percentMap[current.minute]*currentBestScoreSoFar)/100) {
                // generate moves
                List<SearchIteration> moves = generateMoves(current, blueprint, minutesAvailable)
                    .stream()
                    .collect(Collectors.toList());

                if (moves.isEmpty()) {
                    // no moves left
                    // save until the end
                    priorityQueue.add(
                        applyOre(
                            current,
                            new SearchIteration(
                                minutesAvailable,//int minute,
                                current.ore, //int ore,
                                current.clay, //int clay,
                                current.obsidian, //int obsidian,
                                current.geode, //int geode,
                                current.oreRobots, //int oreRobots,
                                current.clayRobots, //int clayRobots,
                                current.obsidianRobots, //int obsidianRobots,
                                current.geodeRobots  //int geodeRobots
                            )
                        )
                    );
                } else {
                    for(SearchIteration move: moves) {
                        int nextMoveScore = score(move, blueprint, minutesAvailable);
                        int nextMoveBestScoreSoFar = scorePruner.getOrDefault(move.minute, 0);
                        if ( !visited.contains(move)
                            &&nextMoveScore >= (percentMap[move.minute]*nextMoveBestScoreSoFar)/100
                        ) {
                            visited.add(move);
                            priorityQueue.add(move);
                            if (nextMoveScore > nextMoveBestScoreSoFar) {
                                scorePruner.put(move.minute, nextMoveScore);
                            }
                        }
                    }
                }
            }
        }

        long t2 = System.currentTimeMillis();
        System.out.println(maxSoFar + " and took " + (t2-t1)/1000 + " seconds " + best);

        return maxSoFar;
    }

    private static long part1(String input) {
        List<Blueprint> blueprints = parseInput(input);
        if(DEBUG) {
            System.out.println(blueprints);
        }

        int [] percentMap = new int[]{
            100, // 0
            100, // 1
            100, // 2
            100, // 3
            100, // 4
            100, // 5
            100, // 6
            100, // 7
            100, // 8
            100, // 9
            100, // 10
            100, // 11
            100, // 12
            100, // 13
            100, // 14
            100, // 15
            100, // 16
            100, // 17
            100, // 18
            100, // 19
            100, // 20
            100, // 21
            100, // 22
            100, // 23
            100 // 24
        };

        return blueprints.stream()
            .mapToLong(blueprint -> blueprint.id * qualityLevel(blueprint, 24, percentMap))
            .sum();
    }

    private static long part2(String input) {
        List<Blueprint> blueprints = parseInput(input);
        int numOfBluePrints = Math.min(3, blueprints.size());

        int [] percentMap = new int[]{
            50, // 0
            50, // 1
            50, // 2
            50, // 3
            50, // 4
            50, // 5
            50, // 6
            50, // 7
            50, // 8
            50, // 9
            50, // 10
            50, // 11
            50, // 12
            50, // 13
            50, // 14
            50, // 15
            50, // 16
            50, // 17
            50, // 18
            50, // 19
            50, // 20
            50, // 21 75 here breaks part2 sample blueprint 1 54 instead of 56
            50, // 22
            50, // 23
            50, // 24
            50, // 25
            50, // 26
            70, // 27
            90, // 28
            90, // 29
            95, // 30
            99, // 31
            100 // 32
        };


        return blueprints.subList(0, numOfBluePrints).stream()
            .mapToLong(blueprint -> qualityLevel(blueprint, 32, percentMap))
            .reduce(1, (a,b) -> a*b);
    }

    public static void main(String[] args) throws IOException {
        int day = 19;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        DEBUG = true;
        //part1(sampleInput);

        DEBUG = false;
//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part1_expected.txt")));
//        System.out.println("Actual:   "
//            + part1(sampleInput));
//        System.out.println("Solution: "
//            + part1(realInput)); // 851

//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part2_expected.txt")));
//        System.out.println("Actual:   " +  part2(sampleInput));
        System.out.println("Solution: " +  part2(realInput)); // ???
        /*
        That's not the right answer; your answer is too low.
        You guessed 11840

        100 percent
        10
        37
        32
        Solution: 11840

        decent with robot filtering
        10 and took 2 seconds SearchIteration[minute=32, ore=13, clay=50, obsidian=16, geode=10, oreRobots=4, clayRobots=9, obsidianRobots=8, geodeRobots=3]
        37 and took 0 seconds SearchIteration[minute=32, ore=12, clay=48, obsidian=10, geode=37, oreRobots=3, clayRobots=6, obsidianRobots=5, geodeRobots=7]
        32 and took 4 seconds SearchIteration[minute=32, ore=19, clay=73, obsidian=14, geode=32, oreRobots=4, clayRobots=9, obsidianRobots=7, geodeRobots=6]
        Solution: 11840
         */

    }
}
