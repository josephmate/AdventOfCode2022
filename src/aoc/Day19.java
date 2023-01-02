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

    private static List<SearchIteration> generateMoves(SearchIteration current, Blueprint blueprint) {
        List<SearchIteration> result = new ArrayList<>();
        int nextMinute = current.minute + 1;

        // saving
        result.add(new SearchIteration(
            nextMinute,//int minute,
            current.ore, //int ore,
            current.clay, //int clay,
            current.obsidian, //int obsidian,
            current.geode, //int geode,
            current.oreRobots, //int oreRobots,
            current.clayRobots, //int clayRobots,
            current.obsidianRobots, //int obsidianRobots,
            current.geodeRobots  //int geodeRobots
        ));

        if (current.ore >= blueprint.oreRobotOreCost) {
            result.add(new SearchIteration(
                nextMinute,//int minute,
                current.ore - blueprint.oreRobotOreCost, //int ore,
                current.clay, //int clay,
                current.obsidian, //int obsidian,
                current.geode, //int geode,
                current.oreRobots + 1, //int oreRobots,
                current.clayRobots, //int clayRobots,
                current.obsidianRobots, //int obsidianRobots,
                current.geodeRobots  //int geodeRobots
            ));
        }
        if (current.ore >= blueprint.clayRobotOreCost) {
            result.add(new SearchIteration(
                nextMinute,//int minute,
                current.ore - blueprint.clayRobotOreCost, //int ore,
                current.clay, //int clay,
                current.obsidian, //int obsidian,
                current.geode, //int geode,
                current.oreRobots, //int oreRobots,
                current.clayRobots + 1, //int clayRobots,
                current.obsidianRobots, //int obsidianRobots,
                current.geodeRobots  //int geodeRobots
            ));
        }
        if (
            current.ore >= blueprint.obsidianRobotOreCost
            && current.clay >= blueprint.obsidianRobotClayCost
        ) {
            result.add(new SearchIteration(
                nextMinute,//int minute,
                current.ore - blueprint.obsidianRobotOreCost, //int ore,
                current.clay - blueprint.obsidianRobotClayCost, //int clay,
                current.obsidian, //int obsidian,
                current.geode, //int geode,
                current.oreRobots, //int oreRobots,
                current.clayRobots, //int clayRobots,
                current.obsidianRobots + 1, //int obsidianRobots,
                current.geodeRobots  //int geodeRobots
            ));
        }
        if (
            current.ore >= blueprint.geodeRobotOreCost
            && current.obsidian >= blueprint.geodeRobotObsidianCost
        ) {
            result.add(new SearchIteration(
                nextMinute,//int minute,
                current.ore - blueprint.geodeRobotOreCost, //int ore,
                current.clay, //int clay,
                current.obsidian - blueprint.geodeRobotObsidianCost, //int obsidian,
                current.geode, //int geode,
                current.oreRobots, //int oreRobots,
                current.clayRobots, //int clayRobots,
                current.obsidianRobots, //int obsidianRobots,
                current.geodeRobots + 1 //int geodeRobots
            ));
        }

        return result;
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
        return new SearchIteration(
            moveWithoutOre.minute,//int minute,
            moveWithoutOre.ore + current.oreRobots, //int ore,
            moveWithoutOre.clay + current.clayRobots, //int clay,
            moveWithoutOre.obsidian + current.obsidianRobots, //int obsidian,
            moveWithoutOre.geode + current.geodeRobots, //int geode,
            moveWithoutOre.oreRobots, //int oreRobots,
            moveWithoutOre.clayRobots, //int clayRobots,
            moveWithoutOre.obsidianRobots, //int obsidianRobots,
            moveWithoutOre.geodeRobots  //int geodeRobots
        );
    }

    private static int score(SearchIteration move, Blueprint blueprint) {
        return
            2 * (
                move.ore
                + move.oreRobots*blueprint.oreRobotOreCost
                + move.clayRobots*blueprint.clayRobotOreCost
                + move.obsidianRobots*blueprint.obsidianRobotOreCost
                + move.oreRobots*(24-move.minute)
            )
            + 1 * (
                move.clay
                + move.obsidianRobots * blueprint.obsidianRobotClayCost
                + move.clay*(24-move.minute)
            )
            + 10 * (
                move.obsidian
                + move.geodeRobots * blueprint.geodeRobotObsidianCost
                + move.obsidianRobots*(24-move.minute)
            )
            + 100 * (
                move.geode
                + move.geodeRobots*(24-move.minute)
            )
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

        final int percent = 50;

        while (!priorityQueue.isEmpty()) {
            SearchIteration current = priorityQueue.remove();
            int currentMoveScore = score(current, blueprint);
            int currentBestScoreSoFar = visited.getOrDefault(current.minute, 0);
            if (current.minute == 24) {
                if (current.geode > maxSoFar) {
                    System.out.println(maxSoFar);
                    maxSoFar = current.geode;
                }
            } else if (currentMoveScore >= (percent*currentBestScoreSoFar)/100) {
                // generate moves
                List<SearchIteration> moves = generateMoves(current, blueprint)
                    .stream()
                    // generate ore
                    .map(move -> applyOre(current, move))
                    .collect(Collectors.toList());
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
