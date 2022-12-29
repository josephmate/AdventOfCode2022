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
 * <a href="https://adventofcode.com/2022/day/13>Day 13: Distress Signal</a>
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
