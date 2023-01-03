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
        SearchIteration current, Blueprint blueprint
    ) {
        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= 24-2; i++) {
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
        SearchIteration current, Blueprint blueprint
    ) {
        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= 24-2; i++) {
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
        SearchIteration current, Blueprint blueprint
    ) {
        if (current.clayRobots == 0) {
            return Optional.empty();
        }

        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= 24-2; i++) {
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
        SearchIteration current, Blueprint blueprint
    ) {
        if (current.obsidianRobots == 0) {
            return Optional.empty();
        }

        int oreSoFar = current.ore;
        int claySoFar = current.clay;
        int obsidianSoFar = current.obsidian;
        int geodeSoFar = current.geode;
        for (int i = current.minute; i <= 24-2; i++) {
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

    private static List<BiFunction<SearchIteration, Blueprint, Optional<SearchIteration>>> BUYERS =
        List.of(
            Day19::buyOreBot,
            Day19::buyClayBot,
            Day19::buyObsidianBot,
            Day19::buyGeodeBot
        );

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
    private static List<SearchIteration> generateMoves(SearchIteration current, Blueprint blueprint) {
        return BUYERS.stream()
            .map( (f) -> f.apply(current, blueprint) )
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

    private static int score(SearchIteration move, Blueprint blueprint) {
        return
//            2 * (
//                move.ore
//                + move.oreRobots*blueprint.oreRobotOreCost
//                + move.clayRobots*blueprint.clayRobotOreCost
//                + move.obsidianRobots*blueprint.obsidianRobotOreCost
//                + move.oreRobots*(24-move.minute)
//            )
//            + 1 * (
//                move.clay
//                + move.obsidianRobots * blueprint.obsidianRobotClayCost
//                + move.clay*(24-move.minute)
//            )
//            + 10 * (
//                move.obsidian
//                + move.geodeRobots * blueprint.geodeRobotObsidianCost
//                + move.obsidianRobots*(24-move.minute)
//            )
//            + 100 * (
//                move.geode
//                + move.geodeRobots*(24-move.minute)
//            )
              move.geode
                  + move.geodeRobots*(24-move.minute)
            ;
    }

    private static int qualityLevel(Blueprint blueprint) {

        int maxSoFar = 0;

        // need to be at least the best score so far
        Map<Integer, Integer> visited = new HashMap<>();

        PriorityQueue<SearchIteration> priorityQueue = new PriorityQueue<>(
            // starting out with BFS for now since I can come up with a good cost function
            Comparator.comparingInt(move -> score(move, blueprint))
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
        visited.put(0, score(start, blueprint));
        priorityQueue.add(start);

        final int percent = 100;

        while (!priorityQueue.isEmpty()) {
            SearchIteration current = priorityQueue.remove();
            int currentMoveScore = score(current, blueprint);
            int currentBestScoreSoFar = visited.getOrDefault(current.minute, 0);
            if (current.minute == 24) {
                if (current.geode > maxSoFar) {
                    maxSoFar = current.geode;
                    System.out.println(maxSoFar);
                }
            } else if (currentMoveScore >= (percent*currentBestScoreSoFar)/100) {
                // generate moves
                List<SearchIteration> moves = generateMoves(current, blueprint)
                    .stream()
                    .collect(Collectors.toList());

                if (moves.isEmpty()) {
                    // no moves left
                    // save until the end
                    priorityQueue.add(
                        applyOre(
                            current,
                            new SearchIteration(
                                24,//int minute,
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
                        int nextMoveScore = score(move, blueprint);
                        int nextMoveBestScoreSoFar = visited.getOrDefault(move.minute, 0);
                        if (nextMoveScore >= (percent*nextMoveBestScoreSoFar)/100) {
                            priorityQueue.add(move);
                            if (nextMoveScore > nextMoveBestScoreSoFar) {
                                visited.put(move.minute, nextMoveScore);
                            }
                        }
                    }
                }
            }
        }

        return maxSoFar;
    }

    private static long part1(String input) {
        List<Blueprint> blueprints = parseInput(input);
        if(DEBUG) {
            System.out.println(blueprints);
        }

        return blueprints.stream()
            .mapToLong(blueprint -> blueprint.id * qualityLevel(blueprint))
            .sum();
    }

    private static long part2(String input) {
        return 0;
    }

    public static void main(String[] args) throws IOException {
        int day = 19;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        DEBUG = true;
        //part1(sampleInput);

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
