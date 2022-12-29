package aoc;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/4>Day 4: Camp Cleanup</a>
 */
public class Day4 implements Solution {

    public record AssignmentPair(
        int firstStart,
        int firstEnd,
        int secondStart,
        int secondEnd
    ) {

    }

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static boolean fullyContains(AssignmentPair assignmentPair) {
        return
            // first is inside second
            (assignmentPair.secondStart <= assignmentPair.firstStart
            && assignmentPair.secondEnd >= assignmentPair.firstEnd)
            // or second is inside first
            || (assignmentPair.firstStart <= assignmentPair.secondStart
                    && assignmentPair.firstEnd >= assignmentPair.secondEnd)
            ;
    }

    private static long part1Impl(String s) {
        return s.lines()
            // 2-4,6-8
            .map(line -> line.split(","))
            .map(pair -> List.of(pair[0].split("-"), pair[1].split("-")))
            .map(tuple -> new AssignmentPair(
                Integer.parseInt(tuple.get(0)[0]),
                Integer.parseInt(tuple.get(0)[1]),
                Integer.parseInt(tuple.get(1)[0]),
                Integer.parseInt(tuple.get(1)[1])
                ))
            .filter(Day4::fullyContains)
            //.peek(System.out::println)
            .count()
            ;
    }

    private static boolean isOverlapping(AssignmentPair assignmentPair) {
        return
            // not of no overlap
           !(
               // first is fully before second
               assignmentPair.firstEnd < assignmentPair.secondStart
               // first is fully after second
               || assignmentPair.firstStart > assignmentPair.secondEnd
            );
    }

    private static long part2Impl(String s) {
        return s.lines()
            // 2-4,6-8
            .map(line -> line.split(","))
            .map(pair -> List.of(pair[0].split("-"), pair[1].split("-")))
            .map(tuple -> new AssignmentPair(
                Integer.parseInt(tuple.get(0)[0]),
                Integer.parseInt(tuple.get(0)[1]),
                Integer.parseInt(tuple.get(1)[0]),
                Integer.parseInt(tuple.get(1)[1])
            ))
            .filter(Day4::isOverlapping)
            //.peek(System.out::println)
            .count()
            ;
    }

}
