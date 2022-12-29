package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <a href="https://adventofcode.com/2022/day/16>Day 16: Proboscidea Volcanium</a>
 */
public class Day16 {

    private static boolean DEBUG = false;

    record Valve(String id, int rate, List<String> tunnels) {

    }

    private static Valve parseValve(String line) {
        /*
          0   1  2    3    4        5      6   7   8     9
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
         */
        String[] cols = line.split(" ");
        String id = cols[1];
        String rateStr = cols[4].split("=")[1];
        int rate = Integer.parseInt(rateStr.substring(0, rateStr.length()-1));
        List<String> tunnels = new ArrayList<>();
        for (int i = 9; i < cols.length; i++) {
            final String tunnel;
            if (cols[i].endsWith(",")) {
                tunnel = cols[i].substring(0, cols[i].length()-1);
            } else {
                tunnel = cols[i];
            }
            tunnels.add(tunnel);
        }

        return new Valve(id, rate, tunnels);
    }

    private static Map<String, Valve> parse(String input) {
        Map<String, Valve> result = new HashMap<>();
        input.lines()
            .map(Day16::parseValve)
            .forEach(valve -> result.put(valve.id, valve));
        return result;
    }

    private static int parseVal(String assignmentStr) {
        String[] cols = assignmentStr.split("=");
        String valueStr = cols[1];
        if (valueStr.endsWith(",") || valueStr.endsWith(":")) {
            return Integer.parseInt(valueStr.substring(0, valueStr.length()-1));
        } else {
            return Integer.parseInt(valueStr);
        }
    }

    record Visited(String id, int minute) {

    }

    record PathSoFar(
        int streamReleased,
        int minute,
        String currentPosn,
        Set<String> open
    ) {

    }

    /*
     * Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
     * Valve BB has flow rate=13; tunnels lead to valves CC, AA
     * Valve CC has flow rate=2; tunnels lead to valves DD, BB
     * Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
     * Valve EE has flow rate=3; tunnels lead to valves FF, DD
     * Valve FF has flow rate=0; tunnels lead to valves EE, GG
     * Valve GG has flow rate=0; tunnels lead to valves FF, HH
     * Valve HH has flow rate=22; tunnel leads to valve GG
     * Valve II has flow rate=0; tunnels lead to valves AA, JJ
     * Valve JJ has flow rate=21; tunnel leads to valve II
     *
     *
     * AA  ---  DD  ----- EE ---- FF ---- GG ---- HH
     *  |  \       \
     *  |    \       \
     *  |      BB --- CC
     *   \
     *    \
     *      II --- JJ
     *
     * Greedy?
     * max from A
         * Valve BB AA->BB     2 minutes -> 28*20 = 560
         * Valve CC AA->BB->CC 3 minutes -> 27*2  = 54
         * Valve DD AA->DD     2 minutes -> 28*20 = 560
         * Valve EE has flow rate=3; tunnels lead to valves FF, DD
         * Valve FF has flow rate=0; tunnels lead to valves EE, GG
         * Valve GG has flow rate=0; tunnels lead to valves FF, HH
         * Valve HH has flow rate=22; tunnel leads to valve GG
         * Valve II has flow rate=0; tunnels lead to valves AA, JJ
         * Valve JJ has flow rate=21; tunnel leads to valve II
         * this finding max from A doesn't look like it works due to a tie
     *
     *  Binary Search?
     *    - on total pressure?
     *
     *  Dynamic Programming ?
     *   F( node, minutes left) =
     *          max ?????
     *  A* search ?
     *    -
     *    - heuristic function ?
     *       - total pressure?
     *       - pressure
     */
    private static long part1(String input) {
        Map<String, Valve> map = parse(input);

        // A* search
        // reduce search space by keeping track of
        //      valve -> minute -> biggestSoFar
        // cost function
        //      2^31 - totalSteamReleased
        //      then minutes consumed
        // generating moves
        //      each non zero valve not used in this path yet

        int maxSoFar = 0;
        int MAX_MINUTES = 30;
        Map<Visited, Integer> visited = new HashMap<>();
        PriorityQueue<PathSoFar> priorityQueue = new PriorityQueue<>(
            Comparator.comparingInt((PathSoFar a) -> Integer.MAX_VALUE - a.streamReleased)
                .thenComparingInt(a -> a.minute)
        );
        visited.put(new Visited("AA", 0), 0);
        priorityQueue.add(new PathSoFar(0, 0, "AA", new HashSet<>()));

        while (!priorityQueue.isEmpty()) {
            PathSoFar pathSoFar = priorityQueue.remove();
            Valve valve = map.get(pathSoFar.currentPosn);


            // try to open
            if (
                pathSoFar.minute <= MAX_MINUTES - 2 // need 1 minute to open, and at least 1 more to get some flow rate
                && valve.rate > 0
                && !pathSoFar.open.contains(pathSoFar.currentPosn)
            ) {
                int timeAfterOpen = pathSoFar.minute + 1;
                int durationOpen = MAX_MINUTES - timeAfterOpen;
                int steamReleased = durationOpen * valve.rate;
                int totalSteamSoFar = pathSoFar.streamReleased + steamReleased;

                Visited visitedInfo = new Visited(pathSoFar.currentPosn, timeAfterOpen);
                int bestSoFar = visited.getOrDefault(visitedInfo, 0);
                if (totalSteamSoFar >= bestSoFar) {
                    maxSoFar = Math.max(maxSoFar, totalSteamSoFar);
                    Set<String> newSet = new HashSet<>(pathSoFar.open);
                    newSet.add(pathSoFar.currentPosn);
                    visited.put(visitedInfo, totalSteamSoFar);
                    priorityQueue.add(new PathSoFar(
                        totalSteamSoFar,  //int streamReleased,
                        timeAfterOpen, //int minute,
                        pathSoFar.currentPosn, //String currentPosn,
                        newSet //Set<String> open
                    ));
                }

            }

            // go somewhere and open a valve
            if (pathSoFar.minute <= MAX_MINUTES - 3) { // need 1 minute to move, 1 to open, and at least 1 more to get some flow rate
                int timeAfterTravel = pathSoFar.minute + 1;
                for(String valveId : valve.tunnels) {
                    Visited visitedInfo = new Visited(valveId, timeAfterTravel);
                    if (pathSoFar.streamReleased >= visited.getOrDefault(visitedInfo, 0)) {
                        visited.put(visitedInfo, pathSoFar.streamReleased);
                        priorityQueue.add(new PathSoFar(
                            pathSoFar.streamReleased,  //int streamReleased,
                            timeAfterTravel, //int minute,
                            valveId, //String currentPosn,
                            pathSoFar.open //Set<String> open
                        ));
                    }
                }
            }

        }

        return maxSoFar;
    }

    record DoublePathSoFar(
        int streamReleased,
        int minute,
        String myPosn,
        String elephantPosn,
        Set<String> open,
        /**
         * Make sure we do not return to a path we already visited, unless we have more steam.
         */
        Map<String, Integer> myPrunePath,
        Map<String, Integer> elephantPrunePath
    ) {

    }

    record DoubleVisited(
        String myId,
        String elephantId,
        int minute
    ){

    }

    private static int part2(String input) {
        Map<String, Valve> map = parse(input);

        // A* search
        // reduce search space by keeping track of
        //      valve -> minute -> biggestSoFar
        // cost function
        //      2^31 - totalSteamReleased
        //      then minutes consumed
        // generating moves
        //      each non zero valve not used in this path yet

        int maxSoFar = 0;
        int MAX_MINUTES = 26;
        Map<DoubleVisited, Integer> visited = new HashMap<>();
        PriorityQueue<DoublePathSoFar> priorityQueue = new PriorityQueue<>(
            Comparator.comparingInt((DoublePathSoFar a) -> Integer.MAX_VALUE - a.streamReleased)
                .thenComparingInt(a -> a.minute)
        );
        visited.put(new DoubleVisited("AA", "AA", 0), 0);
        Map<String, Integer> initialMyPrunePath = new HashMap<>();
        Map<String, Integer> initialElephantPrunePath = new HashMap<>();
        initialMyPrunePath.put("AA", 0);
        initialElephantPrunePath.put("AA", 0);
        priorityQueue.add(new DoublePathSoFar(0, 0, "AA", "AA", new HashSet<>(), initialMyPrunePath, initialElephantPrunePath));

        while (!priorityQueue.isEmpty()) {
            DoublePathSoFar pathSoFar = priorityQueue.remove();
            Valve myValve = map.get(pathSoFar.myPosn);
            Valve elephantValve = map.get(pathSoFar.elephantPosn);


            // try to open
            if (
                pathSoFar.minute <= MAX_MINUTES - 2 // need 1 minute to open, and at least 1 more to get some flow rate
            ) {
                int timeAfterOpen = pathSoFar.minute + 1;
                int durationOpen = MAX_MINUTES - timeAfterOpen;

                if ( !pathSoFar.myPosn.equals(pathSoFar.elephantPosn)
                    && myValve.rate > 0
                    && elephantValve.rate > 0
                    && !pathSoFar.open.contains(pathSoFar.myPosn)
                    && !pathSoFar.open.contains(pathSoFar.elephantPosn)
                ) {
                    // both open valves
                    int steamReleased = durationOpen * (myValve.rate + elephantValve.rate);
                    int totalSteamSoFar = pathSoFar.streamReleased + steamReleased;

                    DoubleVisited visitedInfo = new DoubleVisited(pathSoFar.myPosn, pathSoFar.elephantPosn, timeAfterOpen);
                    int bestSoFar = visited.getOrDefault(visitedInfo, 0);
                    if (
                        totalSteamSoFar >= bestSoFar
                        && totalSteamSoFar > pathSoFar.myPrunePath.getOrDefault(pathSoFar.myPosn, -1)
                        && totalSteamSoFar > pathSoFar.elephantPrunePath.getOrDefault(pathSoFar.elephantPosn, -1)
                    ) {
                        maxSoFar = Math.max(maxSoFar, totalSteamSoFar);
                        Set<String> newSet = new HashSet<>(pathSoFar.open);
                        newSet.add(pathSoFar.myPosn);
                        newSet.add(pathSoFar.elephantPosn);
                        visited.put(visitedInfo, totalSteamSoFar);

                        Map<String, Integer> newMyPrunePath = new HashMap<>(pathSoFar.myPrunePath);
                        newMyPrunePath.put(pathSoFar.myPosn, totalSteamSoFar);
                        Map<String, Integer> newElephantPrunePath = new HashMap<>(pathSoFar.elephantPrunePath);
                        newElephantPrunePath.put(pathSoFar.elephantPosn, totalSteamSoFar);

                        priorityQueue.add(new DoublePathSoFar(
                            totalSteamSoFar,  //int streamReleased,
                            timeAfterOpen, //int minute,
                            pathSoFar.myPosn, //String myPosn,
                            pathSoFar.elephantPosn, //String elephantPosn,
                            newSet, //Set<String> open
                            newMyPrunePath,
                            newElephantPrunePath
                        ));
                    }

                }

                if (
                    myValve.rate > 0
                    && !pathSoFar.open.contains(pathSoFar.myPosn)
                ) {
                    // only I open, elephant moves
                    int steamReleased = durationOpen * (myValve.rate);
                    int totalSteamSoFar = pathSoFar.streamReleased + steamReleased;

                    for (String elephantValveId : elephantValve.tunnels) {
                        DoubleVisited visitedInfo = new DoubleVisited(pathSoFar.myPosn, elephantValveId, timeAfterOpen);
                        int bestSoFar = visited.getOrDefault(visitedInfo, 0);
                        if (
                            totalSteamSoFar >= bestSoFar
                            && totalSteamSoFar > pathSoFar.myPrunePath.getOrDefault(pathSoFar.myPosn, -1)
                            && totalSteamSoFar > pathSoFar.elephantPrunePath.getOrDefault(elephantValveId, -1)
                        ) {
                            maxSoFar = Math.max(maxSoFar, totalSteamSoFar);
                            Set<String> newSet = new HashSet<>(pathSoFar.open);
                            newSet.add(pathSoFar.myPosn);
                            visited.put(visitedInfo, totalSteamSoFar);

                            Map<String, Integer> newMyPrunePath = new HashMap<>(pathSoFar.myPrunePath);
                            newMyPrunePath.put(pathSoFar.myPosn, totalSteamSoFar);
                            Map<String, Integer> newElephantPrunePath = new HashMap<>(pathSoFar.elephantPrunePath);
                            newElephantPrunePath.put(elephantValveId, totalSteamSoFar);

                            priorityQueue.add(new DoublePathSoFar(
                                totalSteamSoFar,  //int streamReleased,
                                timeAfterOpen, //int minute,
                                pathSoFar.myPosn, //String myPosn,
                                elephantValveId, //String elephantPosn
                                newSet, //Set<String> open
                                newMyPrunePath,
                                newElephantPrunePath
                            ));
                        }
                    }
                }

                if (
                    !pathSoFar.myPosn.equals(pathSoFar.elephantPosn)
                    && elephantValve.rate > 0
                    && !pathSoFar.open.contains(pathSoFar.elephantPosn)
                ) {
                    // only elephant opens, I move
                    int steamReleased = durationOpen * (elephantValve.rate);
                    int totalSteamSoFar = pathSoFar.streamReleased + steamReleased;

                    for(String myValveId : myValve.tunnels) {
                        DoubleVisited visitedInfo = new DoubleVisited(myValveId, pathSoFar.elephantPosn, timeAfterOpen);
                        int bestSoFar = visited.getOrDefault(visitedInfo, 0);
                        if (
                            totalSteamSoFar >= bestSoFar
                            && totalSteamSoFar > pathSoFar.myPrunePath.getOrDefault(myValveId, -1)
                            && totalSteamSoFar > pathSoFar.elephantPrunePath.getOrDefault(pathSoFar.elephantPosn, -1)
                        ) {
                            maxSoFar = Math.max(maxSoFar, totalSteamSoFar);
                            Set<String> newSet = new HashSet<>(pathSoFar.open);
                            newSet.add(pathSoFar.elephantPosn);
                            visited.put(visitedInfo, totalSteamSoFar);

                            Map<String, Integer> newMyPrunePath = new HashMap<>(pathSoFar.myPrunePath);
                            newMyPrunePath.put(myValveId, totalSteamSoFar);
                            Map<String, Integer> newElephantPrunePath = new HashMap<>(pathSoFar.elephantPrunePath);
                            newElephantPrunePath.put(pathSoFar.elephantPosn, totalSteamSoFar);

                            priorityQueue.add(new DoublePathSoFar(
                                totalSteamSoFar,  //int streamReleased,
                                timeAfterOpen, //int minute,
                                myValveId, //String myPosn,
                                pathSoFar.elephantPosn, //String elephantPosn,
                                newSet, //Set<String> open
                                newMyPrunePath,
                                newElephantPrunePath
                            ));
                        }
                    }
                }
            }

            // go somewhere and open a valve
            if (pathSoFar.minute <= MAX_MINUTES - 3) { // need 1 minute to move, 1 to open, and at least 1 more to get some flow rate
                int timeAfterTravel = pathSoFar.minute + 1;
                for(String myValveId : myValve.tunnels) {
                    for(String elephantValveId : elephantValve.tunnels) {
                        DoubleVisited visitedInfo = new DoubleVisited(myValveId, elephantValveId, timeAfterTravel);
                        if (
                            pathSoFar.streamReleased >= visited.getOrDefault(visitedInfo, 0)
                            && pathSoFar.streamReleased > pathSoFar.myPrunePath.getOrDefault(myValveId, -1)
                            && pathSoFar.streamReleased > pathSoFar.elephantPrunePath.getOrDefault(elephantValveId, -1)
                        ) {
                            visited.put(visitedInfo, pathSoFar.streamReleased);

                            Map<String, Integer> newMyPrunePath = new HashMap<>(pathSoFar.myPrunePath);
                            newMyPrunePath.put(myValveId, pathSoFar.streamReleased);
                            Map<String, Integer> newElephantPrunePath = new HashMap<>(pathSoFar.elephantPrunePath);
                            newElephantPrunePath.put(elephantValveId, pathSoFar.streamReleased);

                            priorityQueue.add(new DoublePathSoFar(
                                pathSoFar.streamReleased,  //int streamReleased,
                                timeAfterTravel, //int minute,
                                myValveId, //String currentPosn,
                                elephantValveId,
                                pathSoFar.open, //Set<String> open
                                newMyPrunePath,
                                newElephantPrunePath
                            ));
                        }
                    }
                }
            }

        }

        return maxSoFar;
    }

    public static long factorial(long n) {
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result = result * i;
        }
        return result;
    }

    public static void main(String[] args) throws IOException {

        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_16_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_16.txt"));

        long sampleNonZero = parse(sampleInput).values().stream().filter(valve -> valve.rate > 0).count();
        System.out.println("sampleNonZero=" + sampleNonZero + " factorial=" + factorial(sampleNonZero));
        long realNonZero = parse(realInput).values().stream().filter(valve -> valve.rate > 0).count();
        long realFactorial = factorial(realNonZero);
        System.out.println("realNonZero=" + realNonZero + " factorial=" + realFactorial);
        System.out.println("bit=" + (Math.log((double)realFactorial)/Math.log(2)));
        // 40 bits is too much to brute force

        DEBUG = true;
        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_16_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput)); // wrong, but it somehow works for the real input
        System.out.println("Solution: "
            + part1(realInput)); // 1595

        // despite my solution not finding the max for the sample, it was able to find the max for the real input!!!
        // first time I've ever failed the sample but passed the real input :)


        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_16_sample_part2_expected.txt")));
        System.out.println("Actual:   " + part2(sampleInput));
        System.out.println("Solution:   " + part2(realInput));

    }
}
