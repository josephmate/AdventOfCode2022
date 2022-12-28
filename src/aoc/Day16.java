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
 * --- Day 16: Proboscidea Volcanium ---
 *
 * The sensors have led you to the origin of the distress signal: yet another handheld device,
 * just like the one the Elves gave you. However, you don't see any Elves around; instead,
 * the device is surrounded by elephants! They must have gotten lost in these tunnels,
 * and one of the elephants apparently figured out how to turn on the distress signal.
 *
 * The ground rumbles again, much stronger this time. What kind of cave is this, exactly?
 * You scan the cave with your handheld device; it reports mostly igneous rock, some ash,
 * pockets of pressurized gas, magma... this isn't just a cave, it's a volcano!
 *
 * You need to get the elephants out of here, quickly. Your device estimates that you have
 * 30 minutes before the volcano erupts, so you don't have time to go back out the way you came in.
 *
 * You scan the cave for other options and discover a network of pipes and pressure-release valves.
 * You aren't sure how such a system got into a volcano, but you don't have time to complain;
 * your device produces a report (your puzzle input) of each valve's flow rate if it were opened
 * (in pressure per minute) and the tunnels you could use to move between the valves.
 *
 * There's even a valve in the room you and the elephants are currently standing in labeled AA.
 * You estimate it will take you one minute to open a single valve and one minute to follow any
 * tunnel from one valve to another. What is the most pressure you could release?
 *
 * For example, suppose you had the following scan output:
 *
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
 * All of the valves begin closed. You start at valve AA, but it must be damaged or
 * jammed or something: its flow rate is 0, so there's no point in opening it. However,
 * you could spend one minute moving to valve BB and another minute opening it;
 * doing so would release pressure during the remaining 28 minutes at a flow rate of 13,
 * a total eventual pressure release of 28 * 13 = 364. Then, you could spend your third minute
 * moving to valve CC and your fourth minute opening it, providing an additional 26 minutes of
 * eventual pressure release at a flow rate of 2, or 52 total pressure released by valve CC.
 *
 * Making your way through the tunnels like this, you could probably open many or all of the valves
 * by the time 30 minutes have elapsed. However, you need to release as much pressure as possible,
 * so you'll need to be methodical. Instead, consider this approach:
 *
 * == Minute 1 ==
 * No valves are open.
 * You move to valve DD.
 *
 * == Minute 2 ==
 * No valves are open.
 * You open valve DD.
 *
 * == Minute 3 ==
 * Valve DD is open, releasing 20 pressure.
 * You move to valve CC.
 *
 * == Minute 4 ==
 * Valve DD is open, releasing 20 pressure.
 * You move to valve BB.
 *
 * == Minute 5 ==
 * Valve DD is open, releasing 20 pressure.
 * You open valve BB.
 *
 * == Minute 6 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You move to valve AA.
 *
 * == Minute 7 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You move to valve II.
 *
 * == Minute 8 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You move to valve JJ.
 *
 * == Minute 9 ==
 * Valves BB and DD are open, releasing 33 pressure.
 * You open valve JJ.
 *
 * == Minute 10 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve II.
 *
 * == Minute 11 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve AA.
 *
 * == Minute 12 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve DD.
 *
 * == Minute 13 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve EE.
 *
 * == Minute 14 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve FF.
 *
 * == Minute 15 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve GG.
 *
 * == Minute 16 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You move to valve HH.
 *
 * == Minute 17 ==
 * Valves BB, DD, and JJ are open, releasing 54 pressure.
 * You open valve HH.
 *
 * == Minute 18 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve GG.
 *
 * == Minute 19 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve FF.
 *
 * == Minute 20 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve EE.
 *
 * == Minute 21 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You open valve EE.
 *
 * == Minute 22 ==
 * Valves BB, DD, EE, HH, and JJ are open, releasing 79 pressure.
 * You move to valve DD.
 *
 * == Minute 23 ==
 * Valves BB, DD, EE, HH, and JJ are open, releasing 79 pressure.
 * You move to valve CC.
 *
 * == Minute 24 ==
 * Valves BB, DD, EE, HH, and JJ are open, releasing 79 pressure.
 * You open valve CC.
 *
 * == Minute 25 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 26 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 27 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 28 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 29 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * == Minute 30 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * This approach lets you release the most pressure possible in 30 minutes with this valve layout, 1651.
 *
 * Work out the steps to release the most pressure in 30 minutes. What is the most pressure you can release?
 *
 * --- Part Two ---
 *
 * You're worried that even with an optimal approach, the pressure released won't be enough. What if you got one of the elephants to help you?
 *
 * It would take you 4 minutes to teach an elephant how to open the right valves in the right order, leaving you with only 26 minutes to actually execute your plan. Would having two of you working together be better, even if it means having less time? (Assume that you teach the elephant before opening any valves yourself, giving you both the same full 26 minutes.)
 *
 * In the example above, you could teach the elephant to help you as follows:
 *
 * == Minute 1 ==
 * No valves are open.
 * You move to valve II.
 * The elephant moves to valve DD.
 *
 * == Minute 2 ==
 * No valves are open.
 * You move to valve JJ.
 * The elephant opens valve DD.
 *
 * == Minute 3 ==
 * Valve DD is open, releasing 20 pressure.
 * You open valve JJ.
 * The elephant moves to valve EE.
 *
 * == Minute 4 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You move to valve II.
 * The elephant moves to valve FF.
 *
 * == Minute 5 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You move to valve AA.
 * The elephant moves to valve GG.
 *
 * == Minute 6 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You move to valve BB.
 * The elephant moves to valve HH.
 *
 * == Minute 7 ==
 * Valves DD and JJ are open, releasing 41 pressure.
 * You open valve BB.
 * The elephant opens valve HH.
 *
 * == Minute 8 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You move to valve CC.
 * The elephant moves to valve GG.
 *
 * == Minute 9 ==
 * Valves BB, DD, HH, and JJ are open, releasing 76 pressure.
 * You open valve CC.
 * The elephant moves to valve FF.
 *
 * == Minute 10 ==
 * Valves BB, CC, DD, HH, and JJ are open, releasing 78 pressure.
 * The elephant moves to valve EE.
 *
 * == Minute 11 ==
 * Valves BB, CC, DD, HH, and JJ are open, releasing 78 pressure.
 * The elephant opens valve EE.
 *
 * (At this point, all valves are open.)
 *
 * == Minute 12 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * ...
 *
 * == Minute 20 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * ...
 *
 * == Minute 26 ==
 * Valves BB, CC, DD, EE, HH, and JJ are open, releasing 81 pressure.
 *
 * With the elephant helping, after 26 minutes, the best you could do would release a total of 1707 pressure.
 *
 * With you and an elephant working together for 26 minutes, what is the most pressure you could release?
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
