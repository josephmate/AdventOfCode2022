package aoc;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * --- Day 13: Distress Signal ---
 *
 * You climb the hill and again try contacting the Elves. However, you instead receive a signal you
 * weren't expecting: a distress signal.
 *
 * Your handheld device must still not be working properly; the packets from the distress signal
 * got decoded out of order. You'll need to re-order the list of received packets (your puzzle input)
 * to decode the message.
 *
 * Your list consists of pairs of packets; pairs are separated by a blank line. You need to
 * identify how many pairs of packets are in the right order.
 *
 * For example:
 *
 * [1,1,3,1,1]
 * [1,1,5,1,1]
 *
 * [[1],[2,3,4]]
 * [[1],4]
 *
 * [9]
 * [[8,7,6]]
 *
 * [[4,4],4,4]
 * [[4,4],4,4,4]
 *
 * [7,7,7,7]
 * [7,7,7]
 *
 * []
 * [3]
 *
 * [[[]]]
 * [[]]
 *
 * [1,[2,[3,[4,[5,6,7]]]],8,9]
 * [1,[2,[3,[4,[5,6,0]]]],8,9]
 *
 * Packet data consists of lists and integers. Each list starts with [, ends with ],
 * and contains zero or more comma-separated values (either integers or other lists). Each packet is always a list and appears on its own line.
 *
 * When comparing two values, the first value is called left and the second value is called right. Then:
 *
 *     If both values are integers, the lower integer should come first.
 *          If the left integer is lower than the right integer, the inputs are in the right order.
 *          If the left integer is higher than the right integer, the inputs are not in the right order.
 *          Otherwise, the inputs are the same integer; continue checking the next part of the input.
 *     If both values are lists, compare the first value of each list, then the second value, and so on.
 *          If the left list runs out of items first, the inputs are in the right order.
 *          If the right list runs out of items first, the inputs are not in the right order.
 *          If the lists are the same length and no comparison makes a decision about the order,
 *          continue checking the next part of the input.
 *     If exactly one value is an integer,
 *          convert the integer to a list which contains that integer as its only value,
 *          then retry the comparison.
 *          For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list containing 2);
 *          the result is then found by instead comparing [0,0,0] and [2].
 *
 * Using these rules, you can determine which of the pairs in the example are in the right order:
 *
 * == Pair 1 ==
 * - Compare [1,1,3,1,1] vs [1,1,5,1,1]
 *   - Compare 1 vs 1
 *   - Compare 1 vs 1
 *   - Compare 3 vs 5
 *     - Left side is smaller, so inputs are in the right order
 *
 * == Pair 2 ==
 * - Compare [[1],[2,3,4]] vs [[1],4]
 *   - Compare [1] vs [1]
 *     - Compare 1 vs 1
 *   - Compare [2,3,4] vs 4
 *     - Mixed types; convert right to [4] and retry comparison
 *     - Compare [2,3,4] vs [4]
 *       - Compare 2 vs 4
 *         - Left side is smaller, so inputs are in the right order
 *
 * == Pair 3 ==
 * - Compare [9] vs [[8,7,6]]
 *   - Compare 9 vs [8,7,6]
 *     - Mixed types; convert left to [9] and retry comparison
 *     - Compare [9] vs [8,7,6]
 *       - Compare 9 vs 8
 *         - Right side is smaller, so inputs are not in the right order
 *
 * == Pair 4 ==
 * - Compare [[4,4],4,4] vs [[4,4],4,4,4]
 *   - Compare [4,4] vs [4,4]
 *     - Compare 4 vs 4
 *     - Compare 4 vs 4
 *   - Compare 4 vs 4
 *   - Compare 4 vs 4
 *   - Left side ran out of items, so inputs are in the right order
 *
 * == Pair 5 ==
 * - Compare [7,7,7,7] vs [7,7,7]
 *   - Compare 7 vs 7
 *   - Compare 7 vs 7
 *   - Compare 7 vs 7
 *   - Right side ran out of items, so inputs are not in the right order
 *
 * == Pair 6 ==
 * - Compare [] vs [3]
 *   - Left side ran out of items, so inputs are in the right order
 *
 * == Pair 7 ==
 * - Compare [[[]]] vs [[]]
 *   - Compare [[]] vs []
 *     - Right side ran out of items, so inputs are not in the right order
 *
 * == Pair 8 ==
 * - Compare [1,[2,[3,[4,[5,6,7]]]],8,9] vs [1,[2,[3,[4,[5,6,0]]]],8,9]
 *   - Compare 1 vs 1
 *   - Compare [2,[3,[4,[5,6,7]]]] vs [2,[3,[4,[5,6,0]]]]
 *     - Compare 2 vs 2
 *     - Compare [3,[4,[5,6,7]]] vs [3,[4,[5,6,0]]]
 *       - Compare 3 vs 3
 *       - Compare [4,[5,6,7]] vs [4,[5,6,0]]
 *         - Compare 4 vs 4
 *         - Compare [5,6,7] vs [5,6,0]
 *           - Compare 5 vs 5
 *           - Compare 6 vs 6
 *           - Compare 7 vs 0
 *             - Right side is smaller, so inputs are not in the right order
 *
 * What are the indices of the pairs that are already in the right order? (The first pair has
 * index 1, the second pair has index 2, and so on.) In the above example,
 * the pairs in the right order are 1, 2, 4, and 6; the sum of these indices is 13.
 *
 * Determine which pairs of packets are already in the right order. What is the sum of the indices
 * of those pairs?
 *
 * --- Part Two ---
 *
 * Now, you just need to put all of the packets in the right order. Disregard the blank lines in
 * your list of received packets.
 *
 * The distress signal protocol also requires that you include two additional divider packets:
 *
 * [[2]]
 * [[6]]
 *
 * Using the same rules as before, organize all packets - the ones in your list of received packets
 * as well as the two divider packets - into the correct order.
 *
 * For the example above, the result of putting the packets in the correct order is:
 *
 * []
 * [[]]
 * [[[]]]
 * [1,1,3,1,1]
 * [1,1,5,1,1]
 * [[1],[2,3,4]]
 * [1,[2,[3,[4,[5,6,0]]]],8,9]
 * [1,[2,[3,[4,[5,6,7]]]],8,9]
 * [[1],4]
 * [[2]]
 * [3]
 * [[4,4],4,4]
 * [[4,4],4,4,4]
 * [[6]]
 * [7,7,7]
 * [7,7,7,7]
 * [[8,7,6]]
 * [9]
 *
 * Afterward, locate the divider packets. To find the decoder key for this distress signal, you
 * need to determine the indices of the two divider packets and multiply them together.
 * (The first packet is at index 1, the second packet is at index 2, and so on.)
 * In this example, the divider packets are 10th and 14th, and so the decoder key is 140.
 *
 * Organize all of the packets into the correct order. What is the decoder key for the distress signal?
 */
public class Day13 implements Solution {

    private static final boolean DEBUG = false;


    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    record Sublist(List<Record> list) {
        @Override
        public String toString() {
            return "[" + String.join(",", list.stream().map(r -> r.toString()).collect(Collectors.toList())) + "]";
        }
    }

    record Value(int val) {
        @Override
        public String toString() {
            return String.valueOf(val);
        }
    }

    record PacketPair(Sublist first, Sublist second) {

    }

    private static int findMatchingBracket(String line, int startIdx) {
        int bracketCount = 0;
        for (int i = startIdx; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '[') {
                bracketCount++;
            } else if(c == ']') {
                bracketCount--;
                if (bracketCount == 0) {
                    return i;
                }
            }
        }

        throw new IllegalStateException(line + " not formatted correctly");
    }

    private static Sublist parseSublist(String line) {
        List<Record> result = new ArrayList<>();

        boolean hasValue = false;
        int valSoFar = 0;
        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c >= '0' && c<= '9') {
                hasValue = true;
                valSoFar = valSoFar * 10;
                valSoFar = valSoFar + c - '0';
            } else if (c == ',') {
                result.add(new Value(valSoFar));
                valSoFar = 0;
                hasValue = false;
            } else if (c == '[') {
                int endIndex = findMatchingBracket(line, i);
                result.add(parseSublist(line.substring(i+1, endIndex)));
                i = endIndex + 1; // consume the ] and ,
            } else {
                throw new IllegalStateException(c + " cannot be processed");
            }
            i++;
        }

        if (hasValue) {
            result.add(new Value(valSoFar));
        }

        return new Sublist(result);
    }

    private static PacketPair parse(String pairOfLines) {
        try {
            String[] lines = pairOfLines.split("\n");
            return new PacketPair(
                parseSublist(lines[0].substring(1, lines[0].length()-1)),
                parseSublist(lines[1].substring(1, lines[1].length()-1))
            );
        } catch(RuntimeException e) {
            throw new RuntimeException("broken pairOfLines:\n" + pairOfLines + "\n", e);
        }
    }

    private static List<PacketPair> parseInput(String input) {
        return Arrays.stream(input.trim().split("\n\n"))
            .map(Day13::parse)
            .collect(Collectors.toList());
    }

    /*
     *     If both values are lists, compare the first value of each list, then the second value, and so on.
     *          If the left list runs out of items first, the inputs are in the right order.
     *          If the right list runs out of items first, the inputs are not in the right order.
     *          If the lists are the same length and no comparison makes a decision about the order,
     *          continue checking the next part of the input.
     */
    private static Optional<Boolean> bothSublist(List<Record> left, List<Record> right) {
        Iterator<Record> leftIterator = left.iterator();
        Iterator<Record> rightIterator = right.iterator();

        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            Optional<Boolean> indexCompare = isInorder(leftIterator.next(), rightIterator.next());
            if (indexCompare.isPresent()) {
                return indexCompare;
            }
        }

        // If the lists are the same length and no comparison makes a decision about the order,
        //     *          continue checking the next part of the input.
        if (!leftIterator.hasNext() && !rightIterator.hasNext()) {
            return Optional.empty();
        }

        // If the left list runs out of items first, the inputs are in the right order.
        if (!leftIterator.hasNext()) {
            return Optional.of(true);
        }

        // If the right list runs out of items first, the inputs are not in the right order.
        return Optional.of(false);
    }

    /*
     *     If exactly one value is an integer,
     *          convert the integer to a list which contains that integer as its only value,
     *          then retry the comparison.
     *          For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list containing 2);
     *          the result is then found by instead comparing [0,0,0] and [2].
     */
    private static Optional<Boolean> leftValue(int left, List<Record> right) {
        return bothSublist(List.of(new Value(left)), right);
    }

    /*
     *     If exactly one value is an integer,
     *          convert the integer to a list which contains that integer as its only value,
     *          then retry the comparison.
     *          For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list containing 2);
     *          the result is then found by instead comparing [0,0,0] and [2].
     */
    private static Optional<Boolean> rightValue(List<Record> left, int right) {
        return bothSublist(left, List.of(new Value(right)));
    }

    /*
     *     If both values are integers, the lower integer should come first.
     *          If the left integer is lower than the right integer, the inputs are in the right order.
     *          If the left integer is higher than the right integer, the inputs are not in the right order.
     *          Otherwise, the inputs are the same integer; continue checking the next part of the input.
     */
    private static Optional<Boolean> bothValue(int left, int right) {
        if (left < right) {
            return Optional.of(true);
        } else if (left > right) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    /**
     *     If both values are integers, the lower integer should come first.
     *          If the left integer is lower than the right integer, the inputs are in the right order.
     *          If the left integer is higher than the right integer, the inputs are not in the right order.
     *          Otherwise, the inputs are the same integer; continue checking the next part of the input.
     *     If both values are lists, compare the first value of each list, then the second value, and so on.
     *          If the left list runs out of items first, the inputs are in the right order.
     *          If the right list runs out of items first, the inputs are not in the right order.
     *          If the lists are the same length and no comparison makes a decision about the order,
     *          continue checking the next part of the input.
     *     If exactly one value is an integer,
     *          convert the integer to a list which contains that integer as its only value,
     *          then retry the comparison.
     *          For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list containing 2);
     *          the result is then found by instead comparing [0,0,0] and [2].
     */
    private static Optional<Boolean> isInorder(Record left, Record right) {
        return switch (left) {
            case Sublist(var leftList) ->
                switch (right) {
                    case Sublist(var rightList) -> bothSublist(leftList, rightList);
                    case Value(var rightVal) -> rightValue(leftList, rightVal);
                    default -> throw new IllegalStateException("unrecongized type for right=" + right);
                };
            case Value(var leftVal) ->
                switch (right) {
                    case Sublist(var rightList) -> leftValue(leftVal, rightList);
                    case Value(var rightVal) -> bothValue(leftVal, rightVal);
                    default -> throw new IllegalStateException("unrecongized type for right=" + right);
                };
            default -> throw new IllegalStateException("unrecongized type for left=" + left);
        };
    }

    private static boolean isInorderPair(PacketPair pair) {
        return isInorder(pair.first, pair.second)
            .orElse(false);
    }

    private static long part1Impl(String s) {
        if (DEBUG) {
            System.out.println();
        }
        List<PacketPair> packetPairs = parseInput(s);
        int result = 0;
        for (int i = 0; i < packetPairs.size(); i++) {
            if (isInorderPair(packetPairs.get(i))) {
                if (DEBUG) {
                    System.out.println(i);
                }
                result += i + 1;
            }
        }
        return result;
    }


    private static long part2Impl(String s) {
        List<Sublist> packets = new ArrayList<>();
        /*
        The distress signal protocol also requires that you include two additional divider packets:
        [[2]]
        [[6]]
         */
        var twoSublist = new Sublist(List.of(new Sublist(List.of(new Value(2)))));
        var sixSublist = new Sublist(List.of(new Sublist(List.of(new Value(6)))));
        packets.add(twoSublist);
        packets.add(sixSublist);
        for(PacketPair packetPair : parseInput(s)) {
            packets.add(packetPair.first);
            packets.add(packetPair.second);
        }

        Collections.sort(packets,
            (left, right) -> {
                Optional<Boolean> comparision = bothSublist(left.list, right.list);
                if (comparision.isPresent()) {
                    if (comparision.get()) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return 0;
                }
            }
        );

        int twoPos = 0;
        int sixPos = 0;
        for (int i = 0; i < packets.size(); i++) {
            Sublist current = packets.get(i);
            if (current == twoSublist) {
                twoPos = i + 1;
            }
            if (current == sixSublist) {
                sixPos = i + 1;
            }
        }

        return twoPos * sixPos;
    }


    public static void main(String[] args) throws IOException {

    }
}
