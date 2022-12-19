package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

        int minutesPassed = 0;
        int presureSoFar = 0;
        Valve winner =  map.remove("AA");
        while (minutesPassed < 30 && !map.isEmpty()) {

        }

        return presureSoFar;
    }

    private static void part2(String input, int maxValue) {

    }

    public static void main(String[] args) throws IOException {

        DEBUG = true;
        part1(Files.readString(java.nio.file.Path.of("input/day_16_sample.txt")));
//        DEBUG = false;
//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_15_sample_part1_expected.txt")));
//        System.out.println("Actual:   "
//            + part1(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")),10));
//        System.out.println("Solution: "
//            + part1(Files.readString(java.nio.file.Path.of("input/day_15.txt")),2000000));
//
//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_15_sample_part2_expected.txt")));
//        System.out.println("Actual:   ");
//        part2(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")), 20);
//        System.out.println("Solution: ");
//        part2(Files.readString(java.nio.file.Path.of("input/day_15.txt")),4000000);

    }
}